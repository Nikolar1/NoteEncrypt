package com.nikolar.noteencrypt.utils;

import static com.nikolar.noteencrypt.config.Consts.DELIMITER;
import static com.nikolar.noteencrypt.config.Consts.ENCRYPTED_SALT_FILE_NAME;
import static com.nikolar.noteencrypt.config.Consts.NOTES_FILE_NAME;
import static com.nikolar.noteencrypt.config.Consts.SALT_FILE_NAME;

import android.content.Context;

import com.nikolar.noteencrypt.model.Note;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;



public class FileUtils {

    private static Logger logger = Logger.getLogger("FileUtils");
    public static void saveNotes(Context context, List<Note> notes) {
        Thread saveNoteThread = new Thread(() -> {
            try(FileOutputStream fileOutputStream = context.openFileOutput(NOTES_FILE_NAME, Context.MODE_PRIVATE)) {
                Cipher cipher = EncryptionUtils.getInstance().getCipher();
                cipher.init(Cipher.ENCRYPT_MODE, EncryptionUtils.getInstance().getKey());
                byte[] iv = cipher.getIV();
                StringBuilder noteData = new StringBuilder();
                for (Note note : notes) {
                    noteData.append(note).append("\n");
                }
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(deflaterOutputStream, cipher);
                fileOutputStream.write(iv);
                cipherOutputStream.write(noteData.toString().getBytes());

                cipherOutputStream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Encountered error while saving file", e);
            } catch (InvalidKeyException e){
                logger.log(Level.SEVERE, "Invalid key exception encountered while saving file", e);
            }
        });
        saveNoteThread.start();
    }

    public static byte[] getEncryptedSalt(Context context) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException {
        try(FileInputStream fileInputStream = context.openFileInput(ENCRYPTED_SALT_FILE_NAME)) {
            byte[] fileIv = new byte[16];
            fileInputStream.read(fileIv);
            Cipher cipher = EncryptionUtils.getInstance().getCipher();
            cipher.init(Cipher.DECRYPT_MODE, EncryptionUtils.getInstance().getKey(), new IvParameterSpec(fileIv));
            CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);
            byte[] salt = new byte[100];
            cipherInputStream.read(salt);
            cipherInputStream.close();
            return salt;
        }catch (FileNotFoundException e){
            FileOutputStream fileOutputStream = context.openFileOutput(ENCRYPTED_SALT_FILE_NAME, Context.MODE_PRIVATE);
            Cipher cipher = EncryptionUtils.getInstance().getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, EncryptionUtils.getInstance().getKey());
            byte[] iv = cipher.getIV();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);
            fileOutputStream.write(iv);
            cipherOutputStream.write(getSalt(context));
            cipherOutputStream.close();
            return getEncryptedSalt(context);
        }
    }

    public static byte[] getSalt(Context context) throws IOException{
        try {
            logger.info("Retrieving salt");
            byte[] salt = new byte[100];
            FileInputStream fileInputStream = context.openFileInput(SALT_FILE_NAME);
            if(fileInputStream.read(salt) == 100){
                logger.info("Salt retrieved");
                return salt;
            }
            throw new IOException("Wrong number of bytes found");
        }catch (IOException e){
            logger.info("Failed to retrieve salt generating new");
            byte[] salt = new byte[100];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            FileOutputStream fileOutputStream = context.openFileOutput(SALT_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(salt);
            fileOutputStream.close();
            return salt;
        }
    }

    public static List<Note> loadNotes(Context context) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException {
        List<Note> notes = new LinkedList<>();
        FileInputStream fileInputStream = context.openFileInput(NOTES_FILE_NAME);
        byte[] fileIv = new byte[16];
        fileInputStream.read(fileIv);
        Cipher cipher = EncryptionUtils.getInstance().getCipher();
        cipher.init(Cipher.DECRYPT_MODE, EncryptionUtils.getInstance().getKey(), new IvParameterSpec(fileIv));
        InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream);
        CipherInputStream cipherInputStream = new CipherInputStream(inflaterInputStream, cipher);
        InputStreamReader inputStreamReader = new InputStreamReader(cipherInputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line;

        while ((line = reader.readLine()) != null) {
            Optional<Note> note = Note.fromString(line);
            note.ifPresent(notes::add);
        }

        reader.close();
        return notes;
    }
}

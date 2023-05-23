package uz.md.shopapp.file_storage.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class FileStorageUtils {

    public File convertMultiPartToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {

            fos.write(file.getBytes());

        } catch (IOException e) {
            log.error("Error converting file to file", e);
            throw new RuntimeException(e);
        }
        return convertedFile;
    }

    public String getExtension(@NonNull MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf('.'));
    }

    public String getFileName(MultipartFile file, String filename) {
        String extension = getExtension(file);
        return System.currentTimeMillis() + "_" + filename + extension;
    }

    public String getFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}

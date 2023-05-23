package uz.md.shopapp.file_storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * File Storage service that stores files to AWS S3.
 */
public interface FilesStorageService {

    /**
     * save and return the file url that will be used for getting file
     * @param file saving file
     * @param fileName name of the file
     * @return saved file url of AWS S3
     */
    String save(MultipartFile file, String fileName);

    boolean delete(String fileURL);

    String saveOrUpdate(MultipartFile image, String imageUrl);
}

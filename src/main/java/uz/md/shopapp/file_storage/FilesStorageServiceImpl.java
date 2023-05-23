package uz.md.shopapp.file_storage;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.md.shopapp.exceptions.BadRequestException;
import uz.md.shopapp.file_storage.utils.FileStorageUtils;

import java.io.File;

import static uz.md.shopapp.utils.MessageConstants.ERROR_IN_REQUEST_RU;
import static uz.md.shopapp.utils.MessageConstants.ERROR_IN_REQUEST_UZ;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "simulation", havingValue = "false")
public class FilesStorageServiceImpl implements FilesStorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.base_url}")
    private String s3BaseUrl;

    private final AmazonS3 amazonS3;
    private final FileStorageUtils utils;

    @Override
    public String save(MultipartFile file, String filename) {

        if (file == null || file.getOriginalFilename() == null) {
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        }

        String fileName = utils.getFileName(file, filename);

        File convertedFile = utils.convertMultiPartToFile(file);

        amazonS3.putObject(bucketName, fileName, convertedFile);
        convertedFile.delete();
        return s3BaseUrl + fileName;
    }

    @Override
    public boolean delete(String fileUrl) {
        if (fileUrl == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        String fileName = utils.getFileName(fileUrl);
        amazonS3.deleteObject(bucketName, fileName);
        return true;
    }

    @Override
    public String saveOrUpdate(MultipartFile image, String previousImageURL) {
        String savedImageURL = save(image, image.getOriginalFilename());
        if (previousImageURL != null)
            delete(previousImageURL);
        return savedImageURL;
    }

}

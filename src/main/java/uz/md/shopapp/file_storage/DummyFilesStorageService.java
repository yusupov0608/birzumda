package uz.md.shopapp.file_storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(prefix = "app", name = "simulation", havingValue = "true")
public class DummyFilesStorageService implements FilesStorageService {

    @Override
    public String save(MultipartFile file, String fileName) {
        return null;
    }

    @Override
    public boolean delete(String fileURL) {
        return false;
    }

    @Override
    public String saveOrUpdate(MultipartFile image, String imageUrl) {
        return null;
    }
}

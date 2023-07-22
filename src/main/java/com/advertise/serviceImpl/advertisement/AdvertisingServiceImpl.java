package com.advertise.serviceImpl.advertisement;

import com.advertise.entity.advertisement.Advertising;
import com.advertise.entity.advertisement.Media;
import com.advertise.repository.AdvertisingRepository;
import com.advertise.service.advertisement.AdvertisingService;
import com.advertise.service.advertisement.FileStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
public class AdvertisingServiceImpl implements AdvertisingService {
    @Autowired
    private final FileStore fileStore;

    @Autowired
    private final AdvertisingRepository repository;


    public AdvertisingServiceImpl(FileStore fileStore, AdvertisingRepository repository) {
        this.fileStore = fileStore;
        this.repository = repository;
    }
    @Value("${amazon.s3.bucket}")
    private String bucketName;

    @Override
    public Advertising findById(Long id){
        Optional<Advertising> advertisingOptional = repository.findById(id);
        if(advertisingOptional.isPresent()){
            return  advertisingOptional.get();
        }
        return null;

    }

    @Override
    public Advertising findByReference(String reference){
        Advertising advertising= repository.findAdvertisingByReference(reference);

        return  advertising;
    }


    @Override
    @Transactional
    public Advertising save(Advertising object) {
        MultipartFile primaryFile = object.getPrimaryFile();
        MultipartFile secondaryFile = object.getSecondaryFile();
        MultipartFile samplePostFile = object.getSamplePostFile();

        //get file metadata
        if(primaryFile != null && !primaryFile.isEmpty()){
            uploadFileOnS3(object, primaryFile,1);
        }
        //get file metadata
        if(secondaryFile != null && !secondaryFile.isEmpty()){
            uploadFileOnS3(object, secondaryFile,2);
        }

        if(samplePostFile != null && !samplePostFile.isEmpty()){
            uploadFileOnS3(object, samplePostFile,3);
        }
        object.setIsDeleted(false);
        return  repository.save(object);
    }

    private void uploadFileOnS3(Advertising object, MultipartFile file,Integer imageCategory) {
        if(file != null){
            String fileType = null;
            String folderName = null;
            String fileName = null;
            String linkHttp = null;

            fileType = file.getContentType();
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", file.getContentType());
            metadata.put("Content-Length", String.valueOf(file.getSize()));
            //Save Image in S3 and then save Addvertisement in the database
            folderName = "Advertisement/"+ object.getReference();
            String path = String.format("%s/%s", bucketName, folderName);
            // String path = String.format("%s/%s", BucketName.ADVERTISING_FILE.getBucketName(), UUID.randomUUID());
            fileName = String.format("%s", file.getOriginalFilename());
            try {
                URL url = fileStore.upload(path, fileName, Optional.of(metadata), file.getInputStream());
                linkHttp = url.toString();
                File file2 = convertMultiPartToFile(file);

                Media media = new Media();
                media.setFileName(fileName);
                media.setFilePath(folderName);
                media.setFileType(fileType);
                media.setLinkHttp(linkHttp);
                media.setImageCategory(imageCategory);

                if(fileType.equals("video/mp4")){
                    long audioLength = getAudioLength(file2);
                    media.setDuration(Long.toString(audioLength));
                }else{
                    ImageInfo imageInfo = Imaging.getImageInfo(file2);
                    final int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
                    final int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();
                    media.setResolution(physicalWidthDpi+"X"+physicalHeightDpi);
                }

                object.getMediaList().add(media);

            } catch (IOException e) {
                throw new IllegalStateException("Failed to upload file", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

  /*@Override
    public byte[] downloadFile(Long id) {
        Advertising advertising = repository.findById(id).get();
        return fileStore.download(bucketName+"/"+ advertising.getFilePath(), advertising.getFileName());
    }*/

    @Override
    public Page<Advertising> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Advertising> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Advertising update(Advertising entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Advertising entity) {
        if((entity.getIsDeleted() == null || entity.getIsDeleted() == false)
                ){
            Set<Media> mediaList = entity.getMediaList();
            for(Media media : mediaList){
                fileStore.deleteObject(bucketName,media.getFilePath()+"/" + media.getFileName());
                media.setAdvertising(null);
                entity.getMediaList().remove(media);
            }
            if(!entity.getMediaList().isEmpty()){
                entity.getMediaList().clear();
            }
            entity.setIsDeleted(true);
          //  entity.setFileStoreS3(null);
            repository.save(entity);
        }
    }

    private File convertMultiPartToFile(MultipartFile file ) throws IOException {
        File convFile = new File( file.getOriginalFilename() );
        FileOutputStream fos = new FileOutputStream( convFile );
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private static long getAudioLength(File file) throws Exception {
        IsoFile isoFile = new IsoFile(file);
        MovieHeaderBox mhb = isoFile.getMovieBox().getMovieHeaderBox();
        return mhb.getDuration() / mhb.getTimescale();
    }
}

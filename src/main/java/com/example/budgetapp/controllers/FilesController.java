package com.example.budgetapp.controllers;

import com.example.budgetapp.services.FileService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/files")
public class FilesController {
    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> downloadDataFile() throws FileNotFoundException { //Выгрузка файлов
        File file = fileService.getDataFile();

        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON) //Задаем тип файла
                    .contentLength(file.length()) //Узнаем длину файла
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"TransactionsLog.json\"") //Задаем название файла
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build(); //Статус 204
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upLoadDataFile(@RequestParam MultipartFile file) { //Генерация файла, загрузка
        fileService.cleanDataFile(); //Удаляем dataFile, создаем новый
        File dataFile = fileService.getDataFile(); //Берем про него информацию

        try (FileOutputStream fos = new FileOutputStream(dataFile)) { //Открываем исходящий поток
            IOUtils.copy(file.getInputStream(), fos); //Копируем входящий поток из запроса и копируем в исходящий поток
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

//        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {
//            FileOutputStream fos = new FileOutputStream(dataFile);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//            byte[] buffer = new byte[1024];
//            while (bis.read(buffer) > 0) {
//                bos.write(buffer);
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}

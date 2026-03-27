package com.royalhouse.cms.core.application.service;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import com.royalhouse.cms.core.application.repository.ApplicationRepository;
import com.royalhouse.cms.core.application.specification.ApplicationSpecifications;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationXlsxExportService {
    private final ApplicationRepository applicationRepository;

    @Transactional(readOnly = true)
    public byte[] exportXlsx(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status,
            ZoneId zoneId
    ) {
        Specification<Application> specification =
                ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);

        List<Application> filteredApplications = applicationRepository.findAll(
                specification,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return buildWorkBook(filteredApplications, zoneId);
    }

    private byte[] buildWorkBook(List<Application> applications, ZoneId zoneId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Заявки");

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(headerStyle);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
            setBorders(cellStyle);

            CellStyle commentStyle = wb.createCellStyle();
            commentStyle.setWrapText(true);
            commentStyle.setVerticalAlignment(VerticalAlignment.TOP);
            setBorders(commentStyle);

            Row headerRow = sheet.createRow(0);
            String[] cols = {"ID", "Имя", "Телефон", "Email", "Комментарии", "Статус", "Дата"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            sheet.createFreezePane(0, 1);

            int rowIndex = 1;
            for (Application application : applications) {
                Row row = sheet.createRow(rowIndex++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(application.getId() == null ? "" : String.valueOf(application.getId()));
                cell0.setCellStyle(cellStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(nullToEmpty(application.getFullName()));
                cell1.setCellStyle(cellStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(nullToEmpty(application.getPhone()));
                cell2.setCellStyle(cellStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(nullToEmpty(application.getEmail()));
                cell3.setCellStyle(cellStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(nullToEmpty(application.getComment()));
                cell4.setCellStyle(cellStyle);

                String statusLabel = "";
                if (application.getStatus() != null) {
                    statusLabel = application.getStatus() == ApplicationStatus.NEW ? "Новая" : "Отвечено";
                }
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(statusLabel);
                cell5.setCellStyle(cellStyle);

                String date = "";
                if (application.getCreatedAt() != null) {
                    date = dtf.format(ZonedDateTime.ofInstant(application.getCreatedAt(), zoneId));
                }
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(date);
                cell6.setCellStyle(cellStyle);
            }


            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 24 * 256);
            sheet.setColumnWidth(2, 18 * 256);
            sheet.setColumnWidth(3, 26 * 256);
            sheet.setColumnWidth(4, 50 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            sheet.setColumnWidth(6, 20 * 256);

            wb.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate XLSX", e);
        }
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
<<<<<<< HEAD
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryReportNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.DuplicateBakeryException;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryAddReportRepository;
=======
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.DuplicateBakeryException;
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
<<<<<<< HEAD
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportDto;
=======
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final BreadRepository breadRepository;
    private final BakeryRepository bakeryRepository;
    private final UserRepository userRepository;
<<<<<<< HEAD
    private final BakeryAddReportRepository bakeryAddReportRepository;
=======
    //private final BkReportRepository bkReportRepository;
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14

    @Transactional(readOnly = true)
    public List<AdminAllBakeryDto> getAllBakeryList() {
        return bakeryRepository.findAll()
                .stream().map(AdminAllBakeryDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminBakeryDto getBakeryDetail(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        return new AdminBakeryDto(bakery);
    }

    @Transactional
    public void addBakery(String username, AddBakeryRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (bakeryRepository.countAllByNameEqualsAndStreetAddressEquals(request.getName(), request.getStreetAddress()) > 0) throw new DuplicateBakeryException();

        Bakery bakery = Bakery.builder()
                .user(user).name(request.getName()).image(request.getImageList())
                .domicileAddress(request.getDomicileAddress()).hours(request.getHours())
                .websiteURL(request.getWebsiteURL()).instagramURL(request.getInstagramURL())
                .facebookURL(request.getFacebookURL()).blogURL(request.getBlogURL())
                .latitude(123.00).longitude(123.00).phoneNumber(request.getPhoneNumber()) //추후 latitude 와 longitude 수정 필요
                .facilityInfoList(request.getFacilityInfoList()).streetAddress(request.getStreetAddress())
                .build();
        bakeryRepository.save(bakery);

        request.getBreadList().forEach(addBreadRequest -> {
            Bread bread = Bread.builder()
                    .bakery(bakery).name(addBreadRequest.getName()).price(addBreadRequest.getPrice())
                    .image(addBreadRequest.getImageList()).build();
            breadRepository.save(bread);
        });
    }

<<<<<<< HEAD

    @Transactional(readOnly = true)
    public List<BakeryReportDto> getAllBakeryReport() {
        return bakeryAddReportRepository.findAll()
=======
    /*
    @Transactional(readOnly = true)
    public List<BakeryReportDto> getAllBakeryReport() {
        return bkReportRepository.findAll()
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14
                .stream().map(BakeryReportDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BakeryReportDto getBakeryReportDetail(Long reportId) {
<<<<<<< HEAD
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(BakeryReportNotFoundException::new);
        return new BakeryReportDto(bakeryAddReport);
=======
        BkReport bkReport = bkReportRepository.findById(reportId).orElseThrow(BakeryReportNotFoundException::new);
        return new BakeryReportDto(bkReport);
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14
    }

    @Transactional()
    public void updateBakeryReport(Long reportId, UpdateBakeryReportStatusRequest request) {
<<<<<<< HEAD
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(BakeryReportNotFoundException::new);
        bakeryAddReport.updateStatus(request.getStatus());
    }
=======
        BkReport bkReport = bkReportRepository.findById(reportId).orElseThrow(BakeryReportNotFoundException::new);
        bkReport.updateStatus(request.getStatus());
    }
     */
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14
}

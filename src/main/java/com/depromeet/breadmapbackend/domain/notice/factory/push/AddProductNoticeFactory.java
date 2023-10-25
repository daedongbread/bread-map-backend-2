// package com.depromeet.breadmapbackend.domain.notice.factory.push;
//
// import java.util.List;
//
// import org.springframework.stereotype.Component;
//
// import com.depromeet.breadmapbackend.domain.bakery.product.Product;
// import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
// import com.depromeet.breadmapbackend.domain.notice.Notice;
// import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
// import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
// import com.depromeet.breadmapbackend.domain.user.User;
// import com.depromeet.breadmapbackend.domain.user.UserRepository;
// import com.depromeet.breadmapbackend.global.exception.DaedongException;
// import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
// import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
//
// import lombok.RequiredArgsConstructor;
//
// @Component
// @RequiredArgsConstructor
// public class AddProductNoticeFactory implements NoticeFactory {
// 	private static final String NOTICE_TITLE_FORMAT = "내가 제보한 빵이 추가되었어요!";
//
// 	private static final NoticeType SUPPORT_TYPE = NoticeType.ADD_PRODUCT;
// 	private final CustomAWSS3Properties customAwss3Properties;
// 	private final ProductRepository productRepository;
// 	private final UserRepository userRepository;
//
// 	@Override
// 	public boolean support(final NoticeType noticeType) {
// 		return SUPPORT_TYPE == noticeType;
// 	}
//
// 	@Override
// 	public String getImage(final Notice notice) {
// 		return customAwss3Properties.getCloudFront() + "/" +
// 			customAwss3Properties.getDefaultImage().getReport()
// 			+ ".png";
// 	}
//
// 	@Override
// 	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
// 		final User user = userRepository.findById(noticeEventDto.userId())
// 			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
// 		final Product product = productRepository.findById(noticeEventDto.contentId())
// 			.orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
//
// 		return List.of(Notice.createNoticeWithContent(
// 			user,
// 			NOTICE_TITLE_FORMAT,
// 			noticeEventDto.contentId(),
// 			product.getBakery().getName() + " - " + product.getName(),
// 			noticeEventDto.noticeType()
// 		));
// 	}
// }

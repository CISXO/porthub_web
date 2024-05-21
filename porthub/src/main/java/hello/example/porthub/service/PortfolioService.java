package hello.example.porthub.service;

import hello.example.porthub.domain.*;
import hello.example.porthub.repository.MemberRepository;
import hello.example.porthub.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final MemberService memberService;
    private final ImagesDto imagesDto;

    public int upload(PortfolioDto portfolioDto) {
        try {
            String Category = portfolioDto.getCategoryString();
            String thumbnailUrl;
            if (portfolioDto.getThumbnail_cast() == null) {
                thumbnailUrl = "https://porthub2.s3.ap-northeast-2.amazonaws.com/None_Thumbnail.jpeg";
            } else {
                thumbnailUrl = s3Service.uploadFiles(portfolioDto.getThumbnail_cast());
            }
            portfolioDto.setThumbnail_url(thumbnailUrl);
            // UserID 가져오기
            String Email = memberService.getCurrentUserId();
            MemberDto member = memberRepository.findByUserIDtoEmail(Email);
            if (member == null) {
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }
            portfolioDto.setAuthorID(member.getUserID());

            // 포트폴리오 카테고리와 해당하는 값 매핑
            Map<String, Integer> categoryMap = new HashMap<>();
            categoryMap.put("Development", 2);
            categoryMap.put("Music", 3);
            categoryMap.put("Design", 4);
            categoryMap.put("Editing", 5);
            categoryMap.put("Film", 6);
            categoryMap.put("Marketing", 7);
            categoryMap.put("Other", 8);

            int CategoryID = categoryMap.getOrDefault(Category, -1).intValue();
            if (CategoryID == -1) {
                throw new RuntimeException("유효하지 않은 카테고리입니다.");
            }
            portfolioDto.setCategoryID(CategoryID);

            // portfolioID를 가져옴 Images Table에 넣어주기 위함
            portfolioRepository.PortUpload(portfolioDto);
            int PortfolioID = portfolioRepository.PortID();

            imagesDto.setPortfolioID(PortfolioID);

            List<String> multipleFiles = new ArrayList<>();
            List<String> contentList = portfolioDto.getContent();

            if (portfolioDto.getFile() != null) {
                for (MultipartFile file : portfolioDto.getFile()) {
                    String multipleFile = s3Service.uploadFiles(file);
                    multipleFiles.add(multipleFile);
                }
            }

            portfolioDto.setMultipleFiles(multipleFiles); // List<String>

            int UploadSize = multipleFiles.size();

            if (contentList != null && multipleFiles != null) {
                for (int i = 0; i < UploadSize; i++) {
                    imagesDto.setImage_url(multipleFiles.get(i));
                    imagesDto.setContents(contentList.get(i));
                    portfolioRepository.ContentUpload(imagesDto);
                    System.out.println(i);
                    System.out.println(imagesDto);
                }
            }

            return 1; // 성공 시 1 반환
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // 실패 시 0 반환
        }
    }

    public List<CategoryDto> findByCategory() {
        return portfolioRepository.findByCategory();
    }

    public int getCategoryID(String CategoryName){ return portfolioRepository.getCategoryID(CategoryName);}

    public List<MainPortViewDto> findAllPorts() {
        return portfolioRepository.findAllPorts();
    }

    public PortViewDto findportview(int portfolioID) {
        return portfolioRepository.findportview(portfolioID);
    }

    public List<ImagesDto> findportFiles(int portfolioID) {
        return portfolioRepository.findFileviews(portfolioID);
    }

    public List<PortViewDto> finduserport(int portfolioID) {
        return portfolioRepository.finduserport(portfolioID);
    }

    public void postReportdata(CopyrightReportDto copyrightReportDto) {
        portfolioRepository.insertCopyRightReportDto(copyrightReportDto);
    }

    public boolean checkHeart(int portfolioID, String authorID) {
        return portfolioRepository.checkHeart(portfolioID, authorID);
    }

    public void convertLikes(PortLikeDto portLikeDto) {
        if (portfolioRepository.ifnullHeartdata(portLikeDto)) {
            portfolioRepository.updateLikedate(portLikeDto);
        } else {
            portLikeDto.setHeart_Check(true);
            portfolioRepository.createLikedata(portLikeDto);
        }
    }

    public boolean checkFollow(int authorID, String currentEmail) {
        int CurrentID = portfolioRepository.findByUserIDtoEmailcheck(currentEmail);
        System.out.println("authorID: " + authorID + "currentID: " + currentEmail);

        return portfolioRepository.checkFollow(authorID, CurrentID);
    }

    public void following(int authorID, String currentUseremail) {
        int CurrentID = portfolioRepository.findByUserIDtoEmailcheck(currentUseremail);
        portfolioRepository.following(authorID, CurrentID);
    }

    public void unfollow(int authorID, String currentUseremail) {
        int CurrentID = portfolioRepository.findByUserIDtoEmailcheck(currentUseremail);
        portfolioRepository.unfollow(authorID, CurrentID);
    }

    public void updateViewsCount(int portfolioID) {
        portfolioRepository.updateViewsCount(portfolioID);
    }

    public void portdelete(int portfolioID) {
        portfolioRepository.deletePortfolioData(portfolioID);
        portfolioRepository.deletePortfolio(portfolioID);
    }

    public int UpdatePortfolio(PortfolioDto portfolioDto) {
        try {
            String thumbnailUrl;
            if (portfolioDto.getThumbnail_cast() == null) {
//                thumbnailUrl = "https://porthub2.s3.ap-northeast-2.amazonaws.com/None_Thumbnail.jpeg";
                thumbnailUrl = null;
            } else {
                thumbnailUrl = s3Service.uploadFiles(portfolioDto.getThumbnail_cast());
            }
            portfolioDto.setThumbnail_url(thumbnailUrl);


            // portfolioID를 가져옴 Images Table에 넣어주기 위함
            portfolioRepository.PortUpdate(portfolioDto);

            int PortfolioID = portfolioDto.getPortfolioID();

            imagesDto.setPortfolioID(PortfolioID);

            List<String> multipleFiles = new ArrayList<>();
            List<String> contentList = portfolioDto.getContent();

            if (portfolioDto.getFile() != null) {
                for (MultipartFile file : portfolioDto.getFile()) {
                    String multipleFile = s3Service.uploadFiles(file);
                    multipleFiles.add(multipleFile);
                }
            }

            portfolioDto.setMultipleFiles(multipleFiles); // List<String>

            int UploadSize = multipleFiles.size();

            List<Integer> ImageFileID = portfolioRepository.getImagesID(PortfolioID);

            if (contentList != null && multipleFiles != null) {
                for (int i = 0; i < UploadSize; i++) {
                    imagesDto.setImage_url(multipleFiles.get(i));
                    imagesDto.setContents(contentList.get(i));
                    imagesDto.setImagesFileID(ImageFileID.get(i));
                    portfolioRepository.ContentUpdate(imagesDto);
                    System.out.println(i);
                    System.out.println(imagesDto);
                }
            }

            return 1; // 성공 시 1 반환
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // 실패 시 0 반환
        }
    }

    public List<MainPortViewDto> findAllPortsOrderByOldest() {

        return portfolioRepository.findAllPortsOrderByOldest();
    }

    public List<MainPortViewDto> findAllPortsOrderByPopularity() {
        return portfolioRepository.findAllPortsOrderByPopularity();
    }

    public List<MainPortViewDto> findAllPortsOrderByViews() {
        return portfolioRepository.findAllPortsOrderByViews();
    }

    public void portfolioIncreLikes(int portfolioID) {
        portfolioRepository.portfolioIncreLikes(portfolioID);
    }

    public void portfolioDecreLikes(int portfolioID) {
        portfolioRepository.portfolioDecreLikes(portfolioID);
    }

    public int checkCategoryNum(int checkNum) {
        if (checkNum > 0) {
            return portfolioRepository.checkCategoryNum(checkNum);
        } else {
            return 0;
        }
    }

    public List<MainPortViewDto> findPortsByCategory(List<MainPortViewDto> mainPortViewDtoList, int checkNum) {

        List<MainPortViewDto> selectedPortViewDtoList = new ArrayList<>(); // 선택된 포트폴리오를 저장할 리스트

        // 카테고리 넘버가 checkNum과 일치하는 포트폴리오만 selectedPortViewDtoList에 추가
        for (MainPortViewDto portViewDto : mainPortViewDtoList) {
            if (portViewDto.getCategoryID() == checkNum) {
                selectedPortViewDtoList.add(portViewDto);
            }
        }

        return selectedPortViewDtoList;
    }

    public List<MainPortViewDto> findAllSearchPorts(String searchQuery) {
        return portfolioRepository.findAllSearchPorts(searchQuery);
    }

    public List<MainPortViewDto> findAllSearchPortsOrderByPopularity(String searchQuery) {
        return portfolioRepository.findAllSearchPortsOrderByPopularity(searchQuery);
    }

    public List<MainPortViewDto> findAllSearchPortsOrderByViews(String searchQuery) {
        return portfolioRepository.findAllSearchPortsOrderByViews(searchQuery);
    }

    public List<MainPortViewDto> findAllSearchPortsOrderByOldest(String searchQuery) {
        return portfolioRepository.findAllSearchPortsOrderByOldest(searchQuery);
    }
}


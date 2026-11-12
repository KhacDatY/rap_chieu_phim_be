package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.Movie;
import com.example.quanly_rapchieuphim.enums.MovieStatus;
import com.example.quanly_rapchieuphim.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(4)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieInitializer implements ApplicationRunner {

    MovieRepository movieRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu Movie...");
        List<Movie> moviesToSave = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // --- 10 PHIM ĐANG CHIẾU ---
        addMovie(moviesToSave, "Mai", "Câu chuyện về cuộc đời thăng trầm của một người phụ nữ tên Mai, đối mặt với định kiến xã hội.", "Trấn Thành", "Phương Anh Đào, Tuấn Trần", "Tình cảm, Tâm lý", 131, "Tiếng Việt", 18, now.minusDays(10), MovieStatus.NOW_SHOWING,"Mai.jpg");
        addMovie(moviesToSave, "Lật Mặt 7: Một Điều Ước", "Hành trình chữa lành và câu chuyện cảm động về tình mẫu tử của bà Hai và những người con.", "Lý Hải", "Thanh Hiền, Trương Minh Cường", "Gia đình, Cảm động", 138, "Tiếng Việt", 13, now.minusDays(5), MovieStatus.NOW_SHOWING,"lat_mat_7.jpg");
        addMovie(moviesToSave, "Godzilla x Kong: Đế Chế Mới", "Godzilla và Kong phải liên minh để chống lại một mối đe dọa ẩn sâu trong Trái Đất.", "Adam Wingard", "Rebecca Hall", "Hành động, Viễn tưởng", 115, "Tiếng Anh", 13, now.minusDays(20), MovieStatus.NOW_SHOWING,"godzilla.jpg");
        addMovie(moviesToSave, "Kung Fu Panda 4", "Gấu Po phải đối mặt với một kẻ thù mới có khả năng biến hình và tìm người kế vị Thần Long Đại Hiệp.", "Mike Mitchell", "Jack Black", "Hoạt hình, Hài hước", 94, "Lồng Tiếng", 0, now.minusDays(15), MovieStatus.NOW_SHOWING,"panda4.jpg");
        addMovie(moviesToSave, "Dune: Hành Tinh Cát 2", "Paul Atreides liên minh với người Fremen để trả thù những kẻ đã hủy hoại gia đình mình.", "Denis Villeneuve", "Timothée Chalamet", "Viễn tưởng, Hành động", 166, "Tiếng Anh", 13, now.minusDays(30), MovieStatus.NOW_SHOWING,"dune.jpg");
        addMovie(moviesToSave, "Quật Mộ Trùng Ma (Exhuma)", "Nhóm thầy pháp và phong thủy sư đối mặt với thế lực tà ác khi khai quật một ngôi mộ bí ẩn.", "Jang Jae-hyun", "Choi Min-sik", "Kinh dị, Bí ẩn", 134, "Tiếng Hàn", 18, now.minusDays(25), MovieStatus.NOW_SHOWING,"quatmo.jpg");
        addMovie(moviesToSave, "Deadpool & Wolverine", "Deadpool hợp tác cùng Wolverine trong một nhiệm vụ điên rồ xuyên vũ trụ để cứu dòng thời gian.", "Shawn Levy", "Ryan Reynolds, Hugh Jackman", "Hành động, Hài", 127, "Tiếng Anh", 18, now.minusDays(2), MovieStatus.NOW_SHOWING,"Deadpool&Wolverine.jpg");
        addMovie(moviesToSave, "Inside Out 2", "Hành trình khám phá những cảm xúc mới phức tạp hơn của cô bé Riley khi bước vào tuổi dậy thì.", "Kelsey Mann", "Amy Poehler", "Hoạt hình, Gia đình", 96, "Lồng Tiếng", 0, now.minusDays(7), MovieStatus.NOW_SHOWING,"inside2.jpg");
        addMovie(moviesToSave, "Kẻ Trộm Mặt Trăng 4", "Gru và gia đình chào đón thành viên mới, đồng thời phải đối mặt với sự truy đuổi của một kẻ thù cũ.", "Chris Renaud", "Steve Carell", "Hoạt hình, Hài", 95, "Lồng Tiếng", 0, now.minusDays(12), MovieStatus.NOW_SHOWING,"moon4.jpg");
        addMovie(moviesToSave, "Alien: Romulus", "Một nhóm thanh niên đi thám hiểm trạm vũ trụ bỏ hoang và đụng độ sinh vật đáng sợ nhất vũ trụ.", "Fede Álvarez", "Cailee Spaeny", "Kinh dị, Viễn tưởng", 119, "Tiếng Anh", 18, now.minusDays(1), MovieStatus.NOW_SHOWING,"alien.jpg");

        // --- 10 PHIM SẮP CHIẾU ---
        addMovie(moviesToSave, "Venom: The Last Dance", "Cuộc chiến cuối cùng của Eddie Brock và symbiote Venom trước sự săn lùng từ cả hai thế giới.", "Kelly Marcel", "Tom Hardy", "Hành động", 120, "Tiếng Anh", 16, now.plusDays(15), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Mufasa: The Lion King", "Câu chuyện tiền truyện về hành trình vươn lên thành vua của Mufasa vĩ đại.", "Barry Jenkins", "Aaron Pierre", "Hoạt hình", 110, "Tiếng Anh", 0, now.plusDays(40), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Kraven The Hunter", "Nguồn gốc của một trong những phản diện mang tính biểu tượng nhất của vũ trụ Marvel.", "J.C. Chandor", "Aaron Taylor-Johnson", "Hành động", 115, "Tiếng Anh", 16, now.plusDays(30), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Sonic The Hedgehog 3", "Sonic, Tails và Knuckles đối đầu với một đối thủ mới cực kỳ mạnh mẽ: Shadow.", "Jeff Fowler", "Ben Schwartz", "Hoạt hình", 100, "Lồng Tiếng", 0, now.plusDays(60), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Captain America: Brave New World", "Sam Wilson chính thức tiếp nhận khiên và áo choàng của Captain America trong kỷ nguyên mới.", "Julius Onah", "Anthony Mackie", "Hành động", 130, "Tiếng Anh", 13, now.plusMonths(3), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Avatar 3: Fire and Ash", "Hành trình khám phá các tộc người mới trên hành tinh Pandora với những hiểm họa chưa từng có.", "James Cameron", "Sam Worthington", "Viễn tưởng", 180, "Tiếng Anh", 13, now.plusMonths(5), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Superman: Legacy", "Khởi đầu mới của vũ trụ DC với hành trình cân bằng giữa di sản Krypton và thân phận người Trái Đất của Clark Kent.", "James Gunn", "David Corenswet", "Hành động", 140, "Tiếng Anh", 13, now.plusMonths(6), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Lật Mặt 8", "Phần tiếp theo của series bom tấn hành động, gia đình ăn khách nhất điện ảnh Việt Nam.", "Lý Hải", "Đang cập nhật", "Hành động, Gia đình", 120, "Tiếng Việt", 16, now.plusMonths(2), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "The Fantastic Four", "Sự xuất hiện của Bộ Tứ Siêu Đẳng, mở ra những khám phá vũ trụ mới cho MCU.", "Matt Shakman", "Pedro Pascal", "Hành động", 125, "Tiếng Anh", 13, now.plusMonths(4), MovieStatus.COMING_SOON);
        addMovie(moviesToSave, "Mickey 17", "Một nhân bản vô tính tham gia vào sứ mệnh thám hiểm không gian vô cùng khắc nghiệt.", "Bong Joon-ho", "Robert Pattinson", "Viễn tưởng", 139, "Tiếng Anh", 16, now.plusDays(45), MovieStatus.COMING_SOON);

        // --- 10 PHIM ĐÃ KẾT THÚC ---
        addMovie(moviesToSave, "Bố Già", "Câu chuyện tình cha con đầy mâu thuẫn nhưng cũng ngập tràn yêu thương trong một xóm lao động nghèo.", "Trấn Thành", "Trấn Thành, Tuấn Trần", "Tâm lý, Gia đình", 128, "Tiếng Việt", 13, now.minusYears(3), MovieStatus.ENDED);
        addMovie(moviesToSave, "Nhà Bà Nữ", "Mâu thuẫn thế hệ và những góc khuất trong gia đình có ba thế hệ phụ nữ cùng chung sống.", "Trấn Thành", "Lê Giang, Uyển Ân", "Tâm lý, Gia đình", 145, "Tiếng Việt", 16, now.minusYears(1), MovieStatus.ENDED);
        addMovie(moviesToSave, "Avengers: Endgame", "Trận chiến cuối cùng mang tính sử thi của các siêu anh hùng để đảo ngược cú búng tay của Thanos.", "Anthony Russo", "Robert Downey Jr.", "Hành động", 181, "Tiếng Anh", 13, now.minusYears(5), MovieStatus.ENDED);
        addMovie(moviesToSave, "Avatar", "Cuộc chiến bảo vệ hành tinh Pandora tươi đẹp của người Na'vi chống lại sự xâm lăng của loài người.", "James Cameron", "Sam Worthington", "Viễn tưởng", 162, "Tiếng Anh", 13, now.minusYears(10), MovieStatus.ENDED);
        addMovie(moviesToSave, "Titanic", "Mối tình bất diệt giữa Jack và Rose trên chuyến tàu định mệnh chìm xuống đáy đại dương.", "James Cameron", "Leonardo DiCaprio", "Tình cảm", 195, "Tiếng Anh", 13, now.minusYears(20), MovieStatus.ENDED);
        addMovie(moviesToSave, "Spider-Man: No Way Home", "Peter Parker phải đối mặt với hậu quả khi đa vũ trụ mở ra, đưa các ác nhân từ những thế giới khác đến.", "Jon Watts", "Tom Holland", "Hành động", 148, "Tiếng Anh", 13, now.minusYears(2), MovieStatus.ENDED);
        addMovie(moviesToSave, "The Dark Knight", "Người Dơi đối đầu với Joker - một kẻ vô chính phủ muốn nhấn chìm thành phố Gotham vào hỗn loạn.", "Christopher Nolan", "Christian Bale", "Hành động", 152, "Tiếng Anh", 16, now.minusYears(12), MovieStatus.ENDED);
        addMovie(moviesToSave, "Inception", "Một nhóm đạo chích chuyên thâm nhập vào giấc mơ của người khác để đánh cắp hoặc cấy ghép ý tưởng.", "Christopher Nolan", "Leonardo DiCaprio", "Viễn tưởng", 148, "Tiếng Anh", 13, now.minusYears(10), MovieStatus.ENDED);
        addMovie(moviesToSave, "Interstellar", "Nhóm phi hành gia vượt qua hố đen vũ trụ để tìm kiếm một hành tinh mới có thể duy trì sự sống cho nhân loại.", "Christopher Nolan", "Matthew McConaughey", "Viễn tưởng", 169, "Tiếng Anh", 13, now.minusYears(8), MovieStatus.ENDED);
        addMovie(moviesToSave, "Parasite (Ký Sinh Trùng)", "Bi kịch xã hội về sự phân hóa giàu nghèo bùng nổ khi một gia đình nghèo thâm nhập vào một gia đình thượng lưu.", "Bong Joon-ho", "Song Kang-ho", "Tâm lý, Kinh dị", 132, "Tiếng Hàn", 18, now.minusYears(4), MovieStatus.ENDED);

        if (!moviesToSave.isEmpty()) {
            movieRepository.saveAll(moviesToSave);
            log.info("Đã tạo mới {} Movie.", moviesToSave.size());
        } else {
            log.info("Dữ liệu Movie đã đầy đủ.");
        }
    }

    private void addMovie(List<Movie> list, String name, String description, String director, String performer, String genre,
                          int duration, String language, int limitAge, LocalDate releaseDate, MovieStatus status, String imageUrl) {
        if (!movieRepository.existsByName(name)) {
            list.add(Movie.builder()
                    .name(name)
                    .description(description)
                    .director(director)
                    .performer(performer)
                    .genre(genre)
                    .duration(duration)
                    .language(language)
                    .limitAge(limitAge)
                    .releaseDate(releaseDate)
                    .status(status)
                    .imageUrl(imageUrl)
                    .build());
        }
    }

    private void addMovie(List<Movie> list, String name, String description, String director, String performer, String genre,
                          int duration, String language, int limitAge, LocalDate releaseDate, MovieStatus status) {
        if (!movieRepository.existsByName(name)) {
            list.add(Movie.builder()
                    .name(name)
                    .description(description)
                    .director(director)
                    .performer(performer)
                    .genre(genre)
                    .duration(duration)
                    .language(language)
                    .limitAge(limitAge)
                    .releaseDate(releaseDate)
                    .status(status)
                    .imageUrl("imageUrl")
                    .build());
        }
    }
}
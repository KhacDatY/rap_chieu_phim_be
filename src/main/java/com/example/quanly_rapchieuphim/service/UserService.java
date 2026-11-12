package com.example.quanly_rapchieuphim.service;

import com.example.quanly_rapchieuphim.dto.request.*;
import com.example.quanly_rapchieuphim.dto.response.UserResponse;
import com.example.quanly_rapchieuphim.entity.*;
import com.example.quanly_rapchieuphim.enums.MemberRank;
import com.example.quanly_rapchieuphim.exception.AppException;
import com.example.quanly_rapchieuphim.exception.ErrorCode;
import com.example.quanly_rapchieuphim.mapper.UserMapper;
import com.example.quanly_rapchieuphim.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerMember(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        Member member = new Member();
        userMapper.mapToUser(request, member);
        member.setPassword(passwordEncoder.encode(request.getPassword()));

        member.setMemberRank(MemberRank.BRONZE);
        member.setMemberPoint(0);

        return userMapper.toUserResponse(userRepository.save(member));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUserByAdmin(UserCreationRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        User user;
        if (request.getRole() == null) throw new RuntimeException("Phải chỉ định Role (MANAGER, SALE_STAFF)");

        switch (request.getRole().toUpperCase()) {
            case "MANAGER":
                user = new Manager();
                break;
            case "SALE_STAFF":
                user = new SaleStaff();
                break;
            default:
                throw new RuntimeException("Role không hợp lệ để tạo nhân viên");
        }

        userMapper.mapToUser(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    public String changePassword(ChangePasswordRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isPasswordMatch = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Đổi mật khẩu thành công";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserByAdmin(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID userId){
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public UserResponse getUser(UUID id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }
}
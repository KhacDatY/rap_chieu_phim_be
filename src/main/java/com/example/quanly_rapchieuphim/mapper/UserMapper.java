package com.example.quanly_rapchieuphim.mapper;

import com.example.quanly_rapchieuphim.dto.request.UserCreationRequest;
import com.example.quanly_rapchieuphim.dto.request.UserUpdateRequest;
import com.example.quanly_rapchieuphim.dto.response.UserResponse;
import com.example.quanly_rapchieuphim.entity.Member;
import com.example.quanly_rapchieuphim.entity.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    void mapToUser(UserCreationRequest request, @MappingTarget User user);

    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserResponse toUserResponse(User user);

    @AfterMapping
    default void mapMemberSpecificFields(User user, @MappingTarget UserResponse response) {
        if (user instanceof Member member) {
            if (member.getMemberRank() != null) {
                response.setMemberRank(member.getMemberRank().name());
            }
            response.setMemberPoint(member.getMemberPoint());
        }
    }
}
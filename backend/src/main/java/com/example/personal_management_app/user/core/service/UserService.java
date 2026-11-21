package com.example.personal_management_app.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
//import com.example.personal_management_app.log.auditlog.annotations.AuditLog;
//import com.example.personal_management_app.log.auditlog.enums.AuditAction;
import com.example.personal_management_app.user.core.dtos.CreateUserRequest;
import com.example.personal_management_app.user.core.dtos.UpdateUserRequest;
import com.example.personal_management_app.user.core.dtos.UserDto;
import com.example.personal_management_app.user.core.exception.IncorrectPasswordException;
import com.example.personal_management_app.user.core.exception.UserEmailExistsException;
import com.example.personal_management_app.user.core.exception.UserNotFoundException;
import com.example.personal_management_app.user.core.exception.UserUsernameExistsException;
import com.example.personal_management_app.user.core.infrastructure.mapstruct.UserMapper;
import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.core.infrastructure.persistence.projections.UserEmailProjection;
import com.example.personal_management_app.user.core.infrastructure.persistence.projections.UserUsernameProjection;
import com.example.personal_management_app.user.core.infrastructure.persistence.repositories.UserRepository;
import com.example.personal_management_app.user.role.exception.RoleNotFoundException;
import com.example.personal_management_app.user.role.infrastructure.persistence.entities.Role;
import com.example.personal_management_app.user.role.service.RoleService;
import com.example.personal_management_app.user.role.enums.UserRoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user using DTO
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_CREATE, targetIdExpression = "returnObject.id")
    public UserDto createUser(CreateUserRequest request) {
        // Validate uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserEmailExistsException(request.getEmail());
        }

        String username = resolveUsername(request);
        if (userRepository.existsByUsername(username)) {
            throw new UserUsernameExistsException(username);
        }

        // Get role entity
        Role role = roleService.findEntityByName(request.getRoleName() != null ? request.getRoleName() : UserRoleEnum.USER)
            .orElseThrow(() -> new RoleNotFoundException("name", request.getRoleName()));

        User user = userMapper.toEntity(request);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private String resolveUsername(CreateUserRequest request) {
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            return request.getUsername().trim();
        }
        return request.getEmail().substring(0, request.getEmail().indexOf('@'));
    }

    /**
     * Create multiple users in bulk
     */
    @Transactional
    // @AuditLog(action = AuditAction.USER_CREATE, description = "Bulk user creation", targetIdExpression = "returnObject.![id]")
    public List<UserDto> createUsers(List<CreateUserRequest> requests) {
        return requests.stream()
            .map(this::createUser)
            .toList();
    }

    /**
     * Find user by ID
     */
    public UserDto findById(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    /**
     * Find all users
     */
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    /**
     * Find users with pagination
     */
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toDto);
    }

    /**
     * Update user using DTO
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_UPDATE, targetIdExpression = "#id")
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));

        // Check if another user takes email/username
        if (request.email() != null && !user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new UserEmailExistsException(request.email());
        }
        if (request.username() != null && !user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            throw new UserUsernameExistsException(request.username());
        }

        // Get role if specified
        if (request.roleName() != null) {
            Role role = roleService.findEntityByName(request.roleName())
                .orElseThrow(() -> new RoleNotFoundException("name", request.roleName()));
            user.setRole(role);
        }

        userMapper.updateEntity(request, user);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Update user password
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_UPDATE, description = "Password update", targetIdExpression = "#id")
    public UserDto updatePassword(UUID id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IncorrectPasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Soft delete user
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_DELETE, targetIdExpression = "#id")
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));

        user.setDeletedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    /**
     * Delete multiple users in bulk
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_DELETE, description = "Bulk user deletion", targetIdExpression = "#ids")
    public void deleteUsers(List<UUID> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        OffsetDateTime now = OffsetDateTime.now();
        users.forEach(user -> user.setDeletedAt(now));
        userRepository.saveAll(users);
    }

    /**
     * Hard delete user
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_DELETE, description = "Hard user deletion", targetIdExpression = "#id")
    public void hardDeleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    /**
     * Check if user exists
     */
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    /**
     * Count all users
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * Find user by email using repository method
     */
    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userMapper::toDto)
            .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    /**
     * Find user by username using repository method
     */
    public UserDto findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::toDto)
            .orElseThrow(() -> new UserNotFoundException("username", username));
    }

    /**
     * Find users by role using repository method
     */
    public List<UserDto> findByRole(UserRoleEnum roleName) {
        Role role = roleService.findEntityByName(roleName)
            .orElseThrow(() -> new RoleNotFoundException("name", roleName));

        List<User> users = userRepository.findByRole(role);
        return userMapper.toDtoList(users);
    }

    /**
     * Find users by multiple emails in bulk
     */
    public List<UserDto> findByEmails(List<String> emails) {
        List<User> users = userRepository.findByEmailIn(emails);
        return userMapper.toDtoList(users);
    }

    /**
     * Find users by multiple usernames in bulk
     */
    public List<UserDto> findByUsernames(List<String> usernames) {
        List<User> users = userRepository.findByUsernameIn(usernames);
        return userMapper.toDtoList(users);
    }

    /**
     * Find active users using repository method
     */
    public List<UserDto> findActiveUsers() {
        List<User> users = userRepository.findActiveUsers();
        return userMapper.toDtoList(users);
    }

    /**
     * Find deleted users using repository method
     */
    public List<UserDto> findDeletedUsers() {
        List<User> users = userRepository.findDeletedUsers();
        return userMapper.toDtoList(users);
    }

    /**
     * Restore deleted user
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_UPDATE, description = "User restoration", targetIdExpression = "#id")
    public UserDto restoreUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));

        user.setDeletedAt(null);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Check if email is already taken using repository method
     */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if username is already taken using repository method
     */
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Change user role
     */
    @Transactional
//    @AuditLog(action = AuditAction.USER_UPDATE, description = "User role change", targetIdExpression = "#userId")
    public UserDto changeUserRole(UUID userId, UserRoleEnum newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Role role = roleService.findEntityByName(newRole)
            .orElseThrow(() -> new RoleNotFoundException("name", newRole));

        user.setRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Get all unique emails using projection for efficiency
     */
    public List<String> getAllEmails() {
        return userRepository.findAllEmails().stream()
            .map(UserEmailProjection::getEmail)
            .toList();
    }

    /**
     * Get all unique usernames using projection for efficiency
     */
    public List<String> getAllUsernames() {
        return userRepository.findAllUsernames().stream()
            .map(UserUsernameProjection::getUsername)
            .toList();
    }

    public User getReferenceById(UUID id) {
        return userRepository.getReferenceById(id);
    }
}

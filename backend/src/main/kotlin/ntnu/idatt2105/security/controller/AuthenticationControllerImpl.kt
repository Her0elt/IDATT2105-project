package ntnu.idatt2105.security.controller

import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.security.dto.ForgotPassword
import ntnu.idatt2105.security.dto.JwtTokenResponse
import ntnu.idatt2105.security.dto.ResetPasswordDto
import ntnu.idatt2105.security.service.JwtService
import ntnu.idatt2105.user.service.UserServiceImpl
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest


@RestController
class AuthenticationControllerImpl(val jwtConfig: JWTConfig, val jwtService: JwtService, val userService: UserServiceImpl) :
    AuthenticationController {

    override fun refreshToken(request: HttpServletRequest): JwtTokenResponse? {
        val header = request.getHeader(jwtConfig.header)
        return jwtService.refreshToken(header)
    }

    override fun forgotPassword(email: ForgotPassword) {
        return userService.forgotPassword(email)
    }

    override fun resetPassword(passwordResetTokenId: UUID, reset: ResetPasswordDto) {
        return userService.resetPassword(reset, passwordResetTokenId)
    }
}
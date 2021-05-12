package ntnu.idatt2105.sercurity.service

import ntnu.idatt2105.sercurity.JwtUtil
import ntnu.idatt2105.sercurity.exception.RefreshTokenNotFound
import ntnu.idatt2105.sercurity.repository.RefreshTokenRepository
import ntnu.idatt2105.sercurity.token.JwtRefreshToken
import ntnu.idatt2105.sercurity.token.JwtToken
import ntnu.idatt2105.sercurity.token.RefreshToken
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshTokenServiceImpl(val refreshTokenRepository: RefreshTokenRepository, val jwtUtil: JwtUtil): RefreshTokenService {



    override fun invalidateSubsequentTokens(jti: String) {
        val refreshToken: RefreshToken = getByJti(jti)
        traverseAndInvalidateNextRefreshTokens(refreshToken)
        refreshToken.isValid = (false)
        refreshTokenRepository.save(refreshToken)

    }


    private fun traverseAndInvalidateNextRefreshTokens(refreshToken: RefreshToken) {
        var nextToken: RefreshToken? = refreshToken.next
        while (nextToken != null) {
            nextToken.isValid = (false)
            refreshTokenRepository.save(nextToken)
            nextToken = nextToken.next
        }
    }

    override fun rotateRefreshToken(oldRefreshToken: JwtRefreshToken, newRefreshToken: JwtRefreshToken) {
        val oldRefreshToken: RefreshToken = getByJti(oldRefreshToken.getJti())
        val nextRefreshToken: RefreshToken = saveRefreshToken(newRefreshToken)
        oldRefreshToken.isValid = (false)
        oldRefreshToken.next = (nextRefreshToken)
        refreshTokenRepository.save(oldRefreshToken)
    }

    override fun getByJti(jti: String): RefreshToken {
        return refreshTokenRepository.findById(UUID.fromString(jti))
                .orElseThrow { RefreshTokenNotFound() }
    }


    override fun saveRefreshToken(token: JwtToken): RefreshToken {
        val jwtRefreshToken = parseToken(token)
        val refreshTokenToSave: RefreshToken = buildRefreshToken(jwtRefreshToken)
        val savedRefreshToken: RefreshToken = refreshTokenRepository.save(refreshTokenToSave)
        return savedRefreshToken


    }


    fun parseToken(refreshToken: JwtToken): JwtRefreshToken {
        return jwtUtil.parseToken(refreshToken.getToken()) ?: TODO()
    }


    private fun buildRefreshToken(jwtRefreshToken: JwtRefreshToken): RefreshToken {
        return RefreshToken(
                UUID.fromString(jwtRefreshToken.getJti()),
                (true), null)
    }
}
package numble.backend.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import numble.backend.authority.entity.Authority;
import numble.backend.authority.exception.AuthorityExceptionType;
import numble.backend.common.exception.BusinessException;
import numble.backend.jwt.dto.TokenDTO;
import numble.backend.jwt.entity.CustomUserIdPasswordAuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private final long ACCESS_TOKEN_EXPIRE_TIME;            // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME;  // 7일

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey ,
                         @Value("${jwt.access-token-expire-time}") long accessTime,
                         @Value("${jwt.refresh-token-expire-time}") long refreshTime
    ){
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String userId, Set<Authority> authorities){
        return this.createToken(userId, authorities, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String userId, Set<Authority> authorities){
        return this.createToken(userId, authorities, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String getUserIdByToken(String token){
        return this.parseClaims(token).getSubject();
    }

    public TokenDTO createTokenDTO(String accessToken, String refreshToken){
        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(BEARER_TYPE)
                .build();
    }

    public Authentication getAuthentication(String accessToken) throws BusinessException {
        Claims claims = parseClaims(accessToken);
        if (claims.get(AUTHORITIES_KEY) == null || !StringUtils.hasText(claims.get(AUTHORITIES_KEY).toString()))
            throw new BusinessException((AuthorityExceptionType.NOT_FOUND_AUTHORITY));
        log.debug("claims.getAuth = {}", claims.get(AUTHORITIES_KEY));
        log.debug("claims.getUserId = {}", claims.getSubject());

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        authorities.forEach(o -> log.debug("getAuthentication -> authorities = {}", o.getAuthority()));

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new CustomUserIdPasswordAuthToken(principal, "", authorities);
    }

    public int validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return 2;
        } catch (Exception e) {
            log.info("잘못된 토큰입니다.");
            return -1;
        }
    }

    private Claims parseClaims(String accessToken) {
        try{
            return Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public boolean validateTokenExpiration(String token) {
        try {
            parseClaims(token);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    protected String createToken(String email, Set<Authority> authorities, long tokenValid){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put(AUTHORITIES_KEY,
                authorities.stream()
                        .map(Authority::getAuthorityName)
                        .collect(Collectors.joining(",")));

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 토큰 발행 유저 정보
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + tokenValid)) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 키와 알고리즘 설정
                .compact();
    }
}


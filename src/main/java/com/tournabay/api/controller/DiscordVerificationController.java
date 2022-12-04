package com.tournabay.api.controller;

import com.google.gson.Gson;
import com.tournabay.api.dto.DiscordAccessTokenDto;
import com.tournabay.api.dto.DiscordUserInfoDto;
import com.tournabay.api.model.DiscordData;
import com.tournabay.api.model.User;
import com.tournabay.api.model.discord.DiscordVerification;
import com.tournabay.api.repository.DiscordDataRepository;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.DiscordVerificationService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DiscordVerificationController {
    private final DiscordVerificationService discordVerificationService;
    private final DiscordDataRepository discordDataRepository;
    private final UserService userService;

    @GetMapping("/discord-verification")
    @Secured("ROLE_USER")
    public String verifyDiscord(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        DiscordVerification discordVerification = new DiscordVerification(user);
        discordVerificationService.save(discordVerification);
        return "https://discord.com/api/oauth2/authorize?state=" + discordVerification.getState() + "&client_id=1048375456153489488&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fdiscord%2Fredirect&response_type=code&scope=identify";
    }

    @GetMapping("/oauth2/discord/redirect")
    public String discordCallback(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) {
        try {
            DiscordVerification discordVerification = discordVerificationService.findByState(state);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
            RequestBody formBody = new FormBody.Builder()
                    .add("client_id", "1048375456153489488")
                    .add("client_secret", "9Y0bc-vMumAIER-Vh1_qZRPhc28hEpLj")
                    .add("grant_type", "authorization_code")
                    .add("code", code)
                    .add("redirect_uri", "http://localhost:8080/oauth2/discord/redirect")
                    .build();
            Request tokenRequest = new Request.Builder()
                    .url("https://discord.com/api/oauth2/token")
                    .post(formBody)
                    .build();
            Call call = client.newCall(tokenRequest);
            ResponseBody discordResponse = call.execute().body();

            Gson gson = new Gson();
            DiscordAccessTokenDto discordAccessTokenResponse = gson.fromJson(discordResponse.string(), DiscordAccessTokenDto.class);

            Request userInfoRequest = new Request.Builder()
                    .url(" https://discord.com/api/users/@me")
                    .get()
                    .addHeader("Authorization", "Bearer " + discordAccessTokenResponse.getAccess_token())
                    .build();

            Call userInfoCall = client.newCall(userInfoRequest);
            ResponseBody userInfoResponse = userInfoCall.execute().body();

            DiscordUserInfoDto discordUserInfoDto = gson.fromJson(userInfoResponse.string(), DiscordUserInfoDto.class);

            boolean discordDataExists = discordDataRepository.existsByDiscordId(discordUserInfoDto.getId());
            if (discordDataExists) {
                response.sendRedirect("http://localhost:3000/discord-verification?error=discord-account-already-linked");
                return "Discord account already linked";
            }

            User user = userService.getById(discordVerification.getUserId());

            DiscordData discordData = DiscordData.builder()
                    .discordId(discordUserInfoDto.getId())
                    .username(discordUserInfoDto.getUsername())
                    .defaultDiscord(true)
                    .user(user)
                    .build();
            user.getDiscordData().add(discordData);

            discordDataRepository.save(discordData);
            response.sendRedirect("http://localhost:3000/discord-verification?message=discord-account-linked");
        } catch (IOException e) {
            try {
                response.sendRedirect("http://localhost:3000/discord-verification?error=IOException");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
}

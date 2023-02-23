package numble.backend.account.api;

import lombok.RequiredArgsConstructor;
import numble.backend.account.application.AccountService;
import numble.backend.account.dto.*;
import numble.backend.common.dto.BasicResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/api/accounts")
    public BasicResponseDTO<String> createAccount(@RequestBody CreateAccountRequestDTO request){
        return accountService.create(request.getUserId(), request.getPassword());
    }

    @GetMapping("/api/accounts")
    public AccountDTO findAccount(@RequestBody FindAccountRequestDTO request){
        return accountService.findAccount(request.getAccountNumber());
    }

    @GetMapping("/api/accounts/transfer")
    public TransferResponseDTO transfer(@RequestBody TransferRequestDTO request){
        TransferResponseDTO response = accountService.transfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getInputPassword(),
                request.getMoney());
        accountService.notify(response.getMessage());
        return response;
    }


}

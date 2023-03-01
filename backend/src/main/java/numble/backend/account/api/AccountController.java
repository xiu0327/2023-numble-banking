package numble.backend.account.api;

import lombok.RequiredArgsConstructor;
import numble.backend.account.application.AccountService;
import numble.backend.account.dto.*;
import numble.backend.common.dto.BasicResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponseDTO<String> createAccount(@RequestBody CreateAccountRequestDTO request){
        return accountService.create(request.getUserId(), request.getPassword());
    }

    @GetMapping("/api/accounts")
    public AccountDTO findAccount(@RequestBody FindAccountRequestDTO request){
        return accountService.findAccount(request.getAccountNumber(), request.getAccountPassword());
    }

    @PostMapping("/api/accounts/transfer")
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

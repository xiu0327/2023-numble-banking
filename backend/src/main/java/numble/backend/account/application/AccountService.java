package numble.backend.account.application;

import numble.backend.account.dto.AccountDTO;
import numble.backend.account.dto.TransferResponseDTO;
import numble.backend.common.dto.BasicResponseDTO;


/**
 * create : 계좌 생성
 * transfer : 계좌 이체
 * findAccount : 계좌 조회
 */
public interface AccountService {
    BasicResponseDTO<Long> create(String userId, String accountPassword, int money);
    TransferResponseDTO transfer(String from, String to, int money);
    AccountDTO findAccount(String accountNumber);
}

package numble.backend.account.application;

import lombok.RequiredArgsConstructor;
import numble.backend.account.dao.AccountRepository;
import numble.backend.account.dto.AccountBasicResponseDTO;
import numble.backend.account.dto.AccountDTO;
import numble.backend.account.dto.CreateAccountResponseDTO;
import numble.backend.account.dto.TransferResponseDTO;
import numble.backend.account.entity.Account;
import numble.backend.account.exception.AccountExceptionType;
import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.common.exception.BusinessException;
import numble.backend.common.util.TransactionHandler;
import numble.backend.friendship.dao.FriendshipRepository;
import numble.backend.friendship.entity.Friendship;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.entity.Member;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;
    private final TransactionHandler transactionHandler;

    @Override
    @Transactional
    public BasicResponseDTO<String> create(String ownerId, String accountPassword) {
        Member owner = memberRepository.findByUserId(ownerId)
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_MEMBER));
        Account account = Account.builder()
                .owner(owner)
                .accountPassword(accountPassword).build();
        accountRepository.save(account);
        owner.addAccount(account);
        return new CreateAccountResponseDTO(account.getAccountNumber(), "계좌 생성 성공.");
    }

    @Override
    public TransferResponseDTO transfer(String from, String to, String password, int money) {
        Account fromAccount = getAccount(from);
        fromAccount.checkAccountPassword(password);
        Account toAccount = getAccount(to);
        Friendship friendship = toAccount.findFriend(friendshipRepository.findAllByOwnerId(fromAccount.getOwner().getUserId()));
        transactionHandler.runInTransaction(() -> {
            fromAccount.withdrawal(money);
            toAccount.deposit(money);
            friendship.increaseTransaction();
        });
        return new TransferResponseDTO(toAccount.getOwner().getUsername() + "님 에게 " + money + " 원을 이체하였습니다.");
    }


    @Transactional(readOnly = true)
    private Account getAccount(String userAccount) {
        return accountRepository.findAccountByNumber(userAccount)
                .orElseThrow(() -> new BusinessException(AccountExceptionType.NOT_FOUND_ACCOUNT));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO findAccount(String accountNumber, String accountPassword) {
        Account account = getAccount(accountNumber);
        account.checkAccountPassword(accountPassword);
        return AccountDTO.builder()
                .userId(account.getOwner().getUserId())
                .accountNumber(account.getAccountNumber())
                .money(account.getAmount()).build();
    }

    @Override
    public BasicResponseDTO<Boolean> notify(String message) {
        try {
            Thread.sleep(1000);
            return new AccountBasicResponseDTO(true, message);
        } catch (InterruptedException e) {
            throw new BusinessException(AccountExceptionType.FAIL_NOTIFY);
        }
    }
}

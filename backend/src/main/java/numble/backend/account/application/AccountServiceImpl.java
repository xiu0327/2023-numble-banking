package numble.backend.account.application;

import lombok.RequiredArgsConstructor;
import numble.backend.account.dao.AccountRepository;
import numble.backend.account.dto.AccountDTO;
import numble.backend.account.dto.CreateAccountResponseDTO;
import numble.backend.account.dto.TransferResponseDTO;
import numble.backend.account.entity.Account;
import numble.backend.account.exception.AccountExceptionType;
import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.dao.FriendshipRepository;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.entity.Member;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public BasicResponseDTO<Long> create(
            String userId,
            String accountPassword,
            int money) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_MEMBER));
        Account account = Account.builder()
                .member(member)
                .password(accountPassword)
                .money(money).build();
        accountRepository.save(account);
        member.addAccount(account);
        return new CreateAccountResponseDTO(account.getId(), "계좌 생성 성공.");
    }

    @Override
    @Transactional
    public TransferResponseDTO transfer(String from, String to, int money) {
        Account fromAccount = getAccount(from);
        if (fromAccount.getMoney() < money){
            throw new BusinessException(AccountExceptionType.LACK_MONEY);
        }
        Account toAccount = getAccount(to);
        if (!isFriend(fromAccount.getMember().getUserId(), toAccount.getMember().getUserId())){
            throw new BusinessException(AccountExceptionType.NOT_FRIEND);
        }
        int fromPresentMoney = fromAccount.getMoney();
        int toPresentMoney = toAccount.getMoney();
        fromAccount.updateMoney(fromPresentMoney - money);
        toAccount.updateMoney(toPresentMoney + money);
        return new TransferResponseDTO(fromAccount.getMember().getUserId(), toAccount.getMember().getUserId(), money);
    }

    private boolean isFriend(String toId, String fromId) {
        return friendshipRepository.findFriendshipByUserId(toId).stream()
                .anyMatch(f -> f.getFriend().getUserId().equals(fromId));
    }

    private Account getAccount(String userAccount) {
        return accountRepository.findAccountByNumber(userAccount)
                .orElseThrow(() -> new BusinessException(AccountExceptionType.NOT_FOUND_ACCOUNT));
    }

    @Override
    public AccountDTO findAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        return AccountDTO.builder()
                .userId(account.getMember().getUserId())
                .accountNumber(account.getNumber())
                .money(account.getMoney()).build();
    }
}

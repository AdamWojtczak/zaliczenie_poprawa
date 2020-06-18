package edu.iis.mto.testreactor.atm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import edu.iis.mto.testreactor.atm.bank.AccountException;
import edu.iis.mto.testreactor.atm.bank.AuthorizationException;
import edu.iis.mto.testreactor.atm.bank.AuthorizationToken;
import edu.iis.mto.testreactor.atm.bank.Bank;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ATMachineTest {

    @Mock
    private PinCode pinCode;
    @Mock
    private Card card;
    @Mock
    private Bank bank;
    @Mock
    private Money money;
    private ATMachine atMachine;

    @BeforeEach
    public void setUp() throws Exception {
        Mockito.when(money.getCurrency()).thenReturn(Currency.getInstance("PLN"));
        atMachine = new ATMachine(bank, money.getCurrency());
    }

    @Test
    public void successfullWithdrawal() throws ATMOperationException {
        Withdrawal withdrawal = atMachine.withdraw(pinCode, card, money);
        assertEquals(success(pinCode, card, money), withdrawal);
    }

    @Test
    public void behavioralTest() throws ATMOperationException, AuthorizationException, AccountException {
        Withdrawal withdrawal = atMachine.withdraw(pinCode, card, money);

        InOrder callorder = Mockito.inOrder(money, pinCode);

        callorder.verify(money).getCurrency();
        callorder.verify(pinCode).getPIN();
        callorder.verify(card).getNumber();
        callorder.verify(bank).autorize(pinCode.getPIN(), card.getNumber());
        callorder.verify(money).getDenomination();
        callorder.verify(money).getCurrency();
        callorder.verify(bank).charge(any(), money);
    }

    @Test
    public void catchinSomeExceptions() throws ATMOperationException {
        Mockito.doThrow(ATMOperationException.class).validateAmount(money);
        Withdrawal withdrawal = atMachine.withdraw(pinCode, card, money);
    }

    private Withdrawal success(PinCode pin, Card card, Money amount) {
        int notImportant = 10;
        List<BanknotesPack> banknotesPackList = new ArrayList<>();
        for (Banknote banknote : Banknote.getDescFor(amount.getCurrency())) {
            banknotesPackList.add(BanknotesPack.create(10, banknote));
        }
        return Withdrawal.create(banknotesPackList);
    }

    private Withdrawal failure(PinCode pin, Card card, Money amount) {

    }

    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

}

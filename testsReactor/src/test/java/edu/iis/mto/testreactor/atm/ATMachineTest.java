package edu.iis.mto.testreactor.atm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ATMachineTest {

	@Mock
	private Bank bank;
	private PinCode pinCode;
	private Card card;
	private Currency currency = Currency.getInstance("PLN");
	private Money money;
	private ATMachine atMachine;

	@BeforeEach
	public void setUp() throws Exception {
		money = new Money(0);
		card = Card.create("1234");
		pinCode = PinCode.createPIN(1, 2, 3, 4);
//		Mockito.when(pinCode.getPIN()).thenReturn("1234");
		atMachine = new ATMachine(bank, currency);
	}

	@Test
	public void successfullWithdrawal() throws ATMOperationException, AuthorizationException {
		BanknotesPack banknotesPack = BanknotesPack.create(50, Banknote.PL_10);
		List<BanknotesPack> banknotesPackList = new ArrayList<>();
		banknotesPackList.add(banknotesPack);

		Mockito.when(bank.autorize("1234", "1234")).thenReturn(AuthorizationToken.create("1234"));
		Withdrawal withdrawal = atMachine.withdraw(pinCode, card, money);

		Withdrawal expectedWithdrawal = getSuccessfulWithdrawal(money);

		assertEquals(expectedWithdrawal, withdrawal);
	}

	private Withdrawal getSuccessfulWithdrawal(Money money) throws ATMOperationException {
		BanknotesPack banknotesPack;
		List<BanknotesPack> banknotesPackList;
		banknotesPack = BanknotesPack.create(0, Banknote.PL_10);
		banknotesPackList = new ArrayList<>();
		banknotesPackList.add(banknotesPack);

		Withdrawal withdrawal = atMachine.withdraw(pinCode, card, money);
		return withdrawal;
	}

	@Test
	public void behavioralTest() throws ATMOperationException, AuthorizationException, AccountException {

		Mockito.when(bank.autorize("1234", "1234")).thenReturn(AuthorizationToken.create("1234"));
		getSuccessfulWithdrawal(money);

		InOrder callorder = Mockito.inOrder(bank);

//		callorder.verify(money).getCurrency();
//		callorder.verify(pinCode).getPIN();
//		callorder.verify(card).getNumber();
		callorder.verify(bank).autorize(pinCode.getPIN(), card.getNumber());
//		callorder.verify(money).getDenomination();
//		callorder.verify(money).getCurrency();
		callorder.verify(bank).charge(AuthorizationToken.create("1234"), money);
	}

	@Test
	public void catchinSomeExceptions() throws ATMOperationException, AuthorizationException {
//		try {
			Mockito.doThrow(AuthorizationException.class).when(bank).autorize(Mockito.any(), Mockito.any());
//			atMachine.withdraw(pinCode, card, money);
//		} catch (Exception e) {
//			assertEquals(new ATMOperationException(ErrorCode.AHTHORIZATION), e);
//		}
		assertThrows(new ATMOperationException(ErrorCode.AHTHORIZATION).getClass(), () -> atMachine.withdraw(pinCode, card, money));
	}

	@Test
	public void catchinSomeOtherExceptions() throws AccountException {
		try {
			Mockito.doThrow(AccountException.class).when(bank).charge(Mockito.any(), Mockito.any());
			atMachine.withdraw(pinCode, card, money);
		} catch (Exception e) {
			assertEquals(new ATMOperationException(ErrorCode.NO_FUNDS_ON_ACCOUNT), e);
		}
	}

	@Test
	public void catchinSomeOther2Exceptions() throws ATMOperationException {
		try {
			getSuccessfulWithdrawal(new Money(20));
		} catch (Exception e) {
			assertEquals(new ATMOperationException(ErrorCode.WRONG_AMOUNT), e);
		}
	}


	@Test
	public void itCompiles() {
		assertThat(true, Matchers.equalTo(true));
	}

}

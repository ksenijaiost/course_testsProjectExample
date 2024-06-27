package org.example;

import org.example.entities.Bank;
import org.example.entities.UserBuilder;
import org.example.service.CentralBank;
import org.example.service.TimeManager;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        TimeManager timeManager = new TimeManager(LocalDateTime.now());
        CentralBank centralBank = new CentralBank();
        boolean flag = true;
        while (flag) {
            System.out.println("1: Добавить пользователя в банк");
            System.out.println("2: Создать банк");
            System.out.println("3: Создать кредитную карточку");
            System.out.println("4: Создать дебетовую карточку");
            System.out.println("5: Создать депозитную карточку");
            System.out.println("6: Добавить день");
            System.out.println("7: Добавить месяц");
            System.out.println("8: Перевести сумму");
            System.out.println("9: Отменить транзакцию");
            System.out.println("10: Снять деньги со счета");
            System.out.println("11: Положить деньги на счет");
            System.out.println("12: Выход");
            Scanner scanner = new Scanner(System.in);
            String chooseOperation = scanner.nextLine();
            switch (chooseOperation) {
                case "1":
                    System.out.println("Введите имя:");
                    String name = scanner.nextLine();
                    System.out.println("Введите фамилию:");
                    String surname = scanner.nextLine();
                    System.out.println("Введите начальный баланс:");
                    double balance = scanner.nextDouble();
                    System.out.println("Введите адрес:");
                    String address = scanner.nextLine();
                    System.out.println("Введите паспортные данные:");
                    int passportId = scanner.nextInt();
                    System.out.println("Введите банк:");
                    String nameBanks = scanner.nextLine();
                    centralBank.getBank(nameBanks).addUser(new UserBuilder(name, surname, balance)
                            .withAddress(address).withPassportId(passportId).build());
                    System.out.println("Пользователь создан и добавлен в банк. Его id: " + centralBank.getBank(nameBanks)
                            .getListUsers().get(centralBank.getBank(nameBanks).getListUsers().size() - 1)
                            .getUserId().toString());
                    break;
                case "2":
                    System.out.println("Введите название банка:");
                    String title = scanner.nextLine();
                    System.out.println("Введите первый уровень процента:");
                    double firstPercent = scanner.nextDouble();
                    System.out.println("Введите второй уровень процента:");
                    double secondPercent = scanner.nextDouble();
                    System.out.println("Введите третий уровень процента:");
                    double thirdPercent = scanner.nextDouble();
                    System.out.println("Введите первый уровень суммы:");
                    double firstStepSum = scanner.nextDouble();
                    System.out.println("Введите второй уровень суммы:");
                    double secondStepSum = scanner.nextDouble();
                    System.out.println("Процент на остаток для дебетовой карты:");
                    double percentDebitCard = scanner.nextDouble();
                    System.out.println("Кредитный лимит:");
                    double creditLimit = scanner.nextDouble();
                    System.out.println("Комиссия:");
                    double commission = scanner.nextDouble();
                    System.out.println("Лимит для неидентифицированного пользователя:");
                    double untrustedUserLimit = scanner.nextDouble();
                    centralBank.addBank(new Bank(title, firstPercent,
                            secondPercent,
                            thirdPercent, firstStepSum,
                            secondStepSum, percentDebitCard,
                            creditLimit, commission,
                            untrustedUserLimit));
                    timeManager.addObserver(centralBank.getListBanks().get(centralBank.getListBanks().size() - 1));
                    System.out.println("Банк добавлен.");
                    break;
                case "3":
                    System.out.println("Введите название банка:");
                    String titleBank = scanner.nextLine();
                    System.out.println("Введите начальный баланс:");
                    double startBalanceCreditCard = scanner.nextDouble();
                    System.out.println("Введите id пользователя:");
                    String guidUser = scanner.nextLine();
                    centralBank.getBank(titleBank).addCreditCard(timeManager.getTimeStamp(),
                            startBalanceCreditCard,
                            centralBank.getBank(titleBank).findUser(UUID.fromString(guidUser)).getUserId());
                    System.out.printf("Кредитная карта создана. Её id: %s%n", centralBank.getBank(titleBank)
                            .getListCreditCards().get(centralBank.getBank(titleBank).getListCreditCards().size() - 1)
                            .getCardId().toString());
                    break;
                case "4":
                    System.out.println("Введите название банка:");
                    String titleBankDebitCard = scanner.nextLine();
                    System.out.println("Введите начальный баланс:");
                    double startBalanceDebitCard = scanner.nextDouble();
                    System.out.println("Введите id пользователя:");
                    String guidUserDebitCard = scanner.nextLine();
                    centralBank.getBank(titleBankDebitCard).addDebitCard(timeManager.getTimeStamp(),
                            startBalanceDebitCard, centralBank.getBank(titleBankDebitCard)
                                    .findUser(UUID.fromString(guidUserDebitCard)).getUserId());
                    System.out.printf("Дебетовая карта создана. Её id: %s%n", centralBank
                            .getBank(titleBankDebitCard).getListDebitCards().get(centralBank.getBank(titleBankDebitCard)
                                    .getListDebitCards().size() - 1).getCardId().toString());
                    break;
                case "5":
                    System.out.println("Введите название банка:");
                    String titleBankDepositCard = scanner.nextLine();
                    System.out.println("Введите начальный баланс:");
                    double startBalanceDepositCard = scanner.nextDouble();
                    System.out.println("Введите id пользователя:");
                    String guidUserDepositCard = scanner.nextLine();
                    System.out.println("Введите день:");
                    int day = scanner.nextInt();
                    System.out.println("Введите месяц:");
                    int month = scanner.nextInt();
                    System.out.println("Введите год:");
                    int year = scanner.nextInt();
                    centralBank.getBank(titleBankDepositCard).addDepositCard(timeManager.getTimeStamp(),
                            LocalDateTime.of(year, month,
                                    day, 0, 0), startBalanceDepositCard,
                            centralBank.getBank(titleBankDepositCard).findUser(UUID.fromString(guidUserDepositCard)).getUserId());
                    System.out.println("Депозитная карта создана. Её id: " + centralBank.getBank(titleBankDepositCard)
                            .getListDepositCards().get(centralBank.getBank(titleBankDepositCard).getListDepositCards().size() - 1)
                            .getCardId().toString());
                    break;
                case "6":
                    timeManager.addDay();
                    System.out.println("День добавлен.");
                    break;
                case "7":
                    timeManager.addMonth();
                    System.out.println("Месяц добавлен.");
                    break;
                case "8":
                    System.out.println("Введите с какого счета хотите перевести:");
                    String idFrom = scanner.nextLine();
                    System.out.println("Введите на какой счет хотите перевести:");
                    String idTo = scanner.nextLine();
                    System.out.println("Введите сумму перевода:");
                    double transferMoney = scanner.nextDouble();
                    centralBank.transferMoney(
                            transferMoney,
                            centralBank.getCard(UUID.fromString(idFrom)).getId(),
                            centralBank.getCard(UUID.fromString(idTo)).getId());
                    System.out.println("Перевод суммы успешно прошел!");
                    break;
                case "9":
                    System.out.println("Введите с какого счета хотите отменить транзакцию:");
                    String idCard = scanner.nextLine();
                    System.out.println("Введите номер транзакции:");
                    int numberTransaction = scanner.nextInt();
                    centralBank.transactionCancellation(
                            centralBank.getCard(UUID.fromString(idCard)).getId(),
                            numberTransaction);
                    System.out.println("Транзакция отменена!");
                    break;
                case "10":
                    System.out.println("Введите с какого счета хотите снять деньги:");
                    String idCardWithdrawMoney = scanner.nextLine();
                    System.out.println("Введите сумму:");
                    double withdrawMoney = scanner.nextDouble();
                    centralBank.getCard(UUID.fromString(idCardWithdrawMoney)).
                            withdrawMoney(withdrawMoney);
                    System.out.println("Сумма успешно снята со счета!");
                    break;
                case "11":
                    System.out.println("Введите на какой счет хотите положить деньги:");
                    String idCardTopUp = scanner.nextLine();
                    System.out.println("Введите сумму:");
                    double topUpMoney = scanner.nextDouble();
                    centralBank.getCard(UUID.fromString(idCardTopUp)).topUpCard(topUpMoney);
                    System.out.println("Сумма успешно положена!");
                    break;
                case "12":
                    flag = false;
                    break;
            }
        }
    }
}

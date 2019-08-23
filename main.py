#! /usr/bin/env python
from collections import defaultdict
from decimal import Decimal
from typing import Union

ILLEGAL_ACCOUNT_RESET = 'illegal-account-reset'
INSUFFICIENT_LIMIT = 'insufficient-limit'
CARD_BLOCKED = 'card-blocked'
HIGH_FREQUENCY_SMALL_INTERVAL = 'high-frequency-small-interval'
DOUBLED_TRANSACTION = 'doubled-transaction'


class TransactionDeniedError(Exception):
    def __init__(self, violation: str) -> None:
        self.violation = violation


class Account:
    def __init__(self, active_card: bool, available_limit: Union[int, float]) -> None:
        self.active_card = active_card
        self.available_limit = Decimal(available_limit)

    def as_dict(self) -> dict:
        return {
            'activeCard': self.active_card,
            'availableLimit': self.available_limit,
        }

    def has_sufficient_limit(self, amount: Union[int, float]) -> bool:
        return self.available_limit > amount


class Output:
    def __init__(self, acct: Account) -> None:
        self.account = acct
        self.violations = []

    def as_dict(self) -> dict:
        return {
            'account': self.account.as_dict(),
            'violations': self.violations,
        }


class State:
    def __init__(self, acct: Account) -> None:
        self.account = acct
        self.history = defaultdict(list)

    @property
    def account(self) -> Account:
        return self._account

    @account.setter
    def account(self, acct: Account) -> None:
        if self.account_exists():
            raise TransactionDeniedError(ILLEGAL_ACCOUNT_RESET)

        self._account = acct

    def account_exists(self) -> bool:
        return self.account is not None

    def prepare_output(self) -> Output:
        return Output(self.account)

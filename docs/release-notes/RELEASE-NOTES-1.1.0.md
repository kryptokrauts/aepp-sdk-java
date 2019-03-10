# Release notes ([v1.1.0](https://github.com/kryptokrauts/aepp-sdk-java/releases/tag/v1.1.0))

## Breaking changes

- Transactions are now created through a central `TransactionFactory`
  - this refactoring breaks implementations that used older versions of the SDK
  - please check the [documentation](../../../../#documentation) for required changes

## General changes

- [#13](../../../../issues/13) upgrade to new Æternity release 2.0.0 (`Minerva`)
- updated [documentation](../../../../#documentation)

## New Features

- [#6](../../../../issues/6) HD wallet support (BIP44, BIP32 + BIP39)
  - it is now possible to create and recover HD wallets
- [#11](../../../../issues/11) Fees (gas cost) calculation
  - Æternity release 2.0.0 (`Minerva`) introduced a new fee structure
  - the SDK now provides an automated fee calculation if the user doesn't provide a fee on his/her own

## Contributors

Everybody is welcome to contribute to this project. Following people contributed to this release:

- [Marco Walz](https://github.com/marc0olo)
- [Michel Meier](https://github.com/mitch-lbw)

## What's next?

- you can follow our [new features](../../../../projects/1) project board to see what we are currently working on

## Support us

If you like this project we would appreciate your support.

- [ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV](https://explorer.aepps.com/#/account/ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV)

![ak_5z1fmzTKR1GA1P7qiLDCC1s3V7AK2RRpNbXqUhfHQbUeg7mmV](../../donations.png)

(QR-code generated with https://cwaqrgen.com/aeternity)
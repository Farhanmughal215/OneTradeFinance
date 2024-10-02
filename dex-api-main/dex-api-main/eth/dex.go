package eth

import (
	DexAbi "DexAPI/abi"
	"DexAPI/utils/log"
	"context"
	"crypto/ecdsa"
	"github.com/ethereum/go-ethereum/accounts/abi/bind"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/core/types"
	"github.com/ethereum/go-ethereum/ethclient"
	"go.uber.org/zap"
	"math/big"
)

func DexTransferOutToken(client *ethclient.Client, contractAddr, fromAddr, toAddr common.Address, privateKey *ecdsa.PrivateKey, amount int64) (string, error) {

	msg, err := DexAbi.NewDexTransaferTransactor(contractAddr, client)
	if err != nil {
		return "", err
	}

	nonce, err := client.PendingNonceAt(context.Background(), fromAddr)
	if err != nil {
		return "", err
	}

	chainID, err := client.ChainID(context.Background())
	if err != nil {
		return "", err
	}

	gasLimit := uint64(300000) // in units
	gasPrice, err := client.SuggestGasPrice(context.Background())
	if err != nil {
		return "", err
	}

	res, err := msg.TransferoutToken(&bind.TransactOpts{
		From:  fromAddr,
		Nonce: big.NewInt(int64(nonce)),
		Signer: func(address common.Address, tx *types.Transaction) (*types.Transaction, error) {
			return types.SignTx(tx, types.NewEIP155Signer(chainID), privateKey)
		},
		GasPrice: gasPrice,
		GasLimit: gasLimit,
	}, toAddr, big.NewInt(amount))

	if err != nil {
		return "", err
	}

	log.Info("DexTransferOutToken", zap.Uint64("nonce", nonce), zap.String("hash", res.Hash().String()))

	return res.Hash().String(), nil
}

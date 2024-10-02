package eth

import (
	"context"
	"crypto/ecdsa"
	"errors"
	"fmt"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/core/types"
	"github.com/ethereum/go-ethereum/crypto"
	"github.com/ethereum/go-ethereum/ethclient"
	"math/big"
)

func transfer(client *ethclient.Client, toAddrStr string, privateKeyStr string, price float64) (string, uint64, error) {
	toAddr := common.HexToAddress(toAddrStr)
	privateKey, fromAddress, err := getPrivate(privateKeyStr)

	nonce, err := client.PendingNonceAt(context.Background(), fromAddress)
	if err != nil {
		return "", 0, err
	}

	fmt.Println("nonce = ", nonce)

	value := toValue(price)
	gasLimit := uint64(300000) // in units
	gasPrice, err := client.SuggestGasPrice(context.Background())
	if err != nil {
		return "", 0, err
	}

	tx := types.NewTx(&types.LegacyTx{
		Nonce:    nonce,
		To:       &toAddr,
		Value:    value,
		Gas:      gasLimit,
		GasPrice: gasPrice,
	})

	chainID, err := client.ChainID(context.Background())
	if err != nil {
		return "", 0, err
	}

	signedTx, err := types.SignTx(tx, types.NewEIP155Signer(chainID), privateKey)
	if err != nil {
		return "", 0, err
	}

	err = client.SendTransaction(context.Background(), signedTx)
	if err != nil {
		return "", 0, err
	}

	return signedTx.Hash().String(), nonce, err
}

func getPrivate(privateKeyStr string) (*ecdsa.PrivateKey, common.Address, error) {
	privateKey, err := crypto.HexToECDSA(privateKeyStr[2:])
	if err != nil {
		return nil, common.Address{}, err
	}

	publicKey := privateKey.Public()
	publicKeyECDSA, ok := publicKey.(*ecdsa.PublicKey)
	if !ok {
		return nil, common.Address{}, errors.New("error casting public key to ECDSA")
	}

	fromAddress := crypto.PubkeyToAddress(*publicKeyECDSA)
	return privateKey, fromAddress, nil
}

func toValue(value float64) *big.Int {
	// 创建一个 big.Float
	floatValue := new(big.Float).SetFloat64(value)

	// 将浮点数乘以 10^18
	scale := new(big.Float).SetFloat64(1e18)
	floatValue.Mul(floatValue, scale)
	intValue := new(big.Int)
	v, _ := floatValue.Int(intValue)
	return v
}

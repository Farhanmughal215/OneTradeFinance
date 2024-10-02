package eth

import (
	"crypto/ecdsa"
	"encoding/hex"
	"errors"
	"github.com/ethereum/go-ethereum/common/hexutil"
	"github.com/ethereum/go-ethereum/crypto"
	"golang.org/x/crypto/sha3"
)

func Create() (string, string, error) {
	privateKey, err := crypto.GenerateKey()
	if err != nil {
		return "", "", err
	}

	privateKeyBytes := crypto.FromECDSA(privateKey)
	privateKeyStr := hexutil.Encode(privateKeyBytes) // 0xfad9c8855b740a0b7ed4c221dbad0f33a83a49cad6b3fe8d5817ac83d38b6a19
	//fmt.Println("privateKeyStr = ", privateKeyStr)

	publicKey := privateKey.Public()
	publicKeyECDSA, ok := publicKey.(*ecdsa.PublicKey)
	if !ok {
		return "", "", errors.New("error casting public key to ECDSA")
	}

	publicKeyBytes := crypto.FromECDSAPub(publicKeyECDSA)
	//publicKeyStr := hexutil.Encode(publicKeyBytes)
	//fmt.Println("publicKeyStr = ", publicKeyStr) // 0x049a7df67f79246283fdc93af76d4f8cdd62c4886e8cd870944e817dd0b97934fdd7719d0810951e03418205868a5c1b40b192451367f28e0088dd75e15de40c05

	address := crypto.PubkeyToAddress(*publicKeyECDSA).Hex()
	//fmt.Println("address = ", address) // 0x96216849c49358B10257cb55b28eA603c874b05E

	//hash := sha3.NewKeccak256()
	//hash.Write(publicKeyBytes[1:])
	//fmt.Println(hexutil.Encode(hash.Sum(nil)[12:])) // 0x96216849c49358b10257cb55b28ea603c874b05e

	hash := sha3.NewLegacyKeccak256()
	hash.Write(publicKeyBytes[1:])
	hashResult := hash.Sum(nil)[12:]
	hashStr := hex.EncodeToString(hashResult)
	_ = hashStr
	//fmt.Println("hashStr = ", hashStr)

	return privateKeyStr, address, nil
}

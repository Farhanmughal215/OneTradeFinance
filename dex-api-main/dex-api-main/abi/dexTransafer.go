// Code generated - DO NOT EDIT.
// This file is a generated binding and any manual changes will be lost.

package dexTransafer

import (
	"errors"
	"math/big"
	"strings"

	ethereum "github.com/ethereum/go-ethereum"
	"github.com/ethereum/go-ethereum/accounts/abi"
	"github.com/ethereum/go-ethereum/accounts/abi/bind"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/core/types"
	"github.com/ethereum/go-ethereum/event"
)

// Reference imports to suppress errors if they are not otherwise used.
var (
	_ = errors.New
	_ = big.NewInt
	_ = strings.NewReader
	_ = ethereum.NotFound
	_ = bind.Bind
	_ = common.Big1
	_ = types.BloomLookup
	_ = event.NewSubscription
	_ = abi.ConvertType
)

// DexTransaferMetaData contains all meta data concerning the DexTransafer contract.
var DexTransaferMetaData = &bind.MetaData{
	ABI: "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"tokenAddress\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"address\",\"name\":\"oldOwner\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"OwnershipTransferred\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferTokenLog\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferoutTokenLog\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"balances\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"deposit\",\"outputs\":[],\"stateMutability\":\"payable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"transferOwnership\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferToken\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferoutToken\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]",
}

// DexTransaferABI is the input ABI used to generate the binding from.
// Deprecated: Use DexTransaferMetaData.ABI instead.
var DexTransaferABI = DexTransaferMetaData.ABI

// DexTransafer is an auto generated Go binding around an Ethereum contract.
type DexTransafer struct {
	DexTransaferCaller     // Read-only binding to the contract
	DexTransaferTransactor // Write-only binding to the contract
	DexTransaferFilterer   // Log filterer for contract events
}

// DexTransaferCaller is an auto generated read-only Go binding around an Ethereum contract.
type DexTransaferCaller struct {
	contract *bind.BoundContract // Generic contract wrapper for the low level calls
}

// DexTransaferTransactor is an auto generated write-only Go binding around an Ethereum contract.
type DexTransaferTransactor struct {
	contract *bind.BoundContract // Generic contract wrapper for the low level calls
}

// DexTransaferFilterer is an auto generated log filtering Go binding around an Ethereum contract events.
type DexTransaferFilterer struct {
	contract *bind.BoundContract // Generic contract wrapper for the low level calls
}

// DexTransaferSession is an auto generated Go binding around an Ethereum contract,
// with pre-set call and transact options.
type DexTransaferSession struct {
	Contract     *DexTransafer     // Generic contract binding to set the session for
	CallOpts     bind.CallOpts     // Call options to use throughout this session
	TransactOpts bind.TransactOpts // Transaction auth options to use throughout this session
}

// DexTransaferCallerSession is an auto generated read-only Go binding around an Ethereum contract,
// with pre-set call options.
type DexTransaferCallerSession struct {
	Contract *DexTransaferCaller // Generic contract caller binding to set the session for
	CallOpts bind.CallOpts       // Call options to use throughout this session
}

// DexTransaferTransactorSession is an auto generated write-only Go binding around an Ethereum contract,
// with pre-set transact options.
type DexTransaferTransactorSession struct {
	Contract     *DexTransaferTransactor // Generic contract transactor binding to set the session for
	TransactOpts bind.TransactOpts       // Transaction auth options to use throughout this session
}

// DexTransaferRaw is an auto generated low-level Go binding around an Ethereum contract.
type DexTransaferRaw struct {
	Contract *DexTransafer // Generic contract binding to access the raw methods on
}

// DexTransaferCallerRaw is an auto generated low-level read-only Go binding around an Ethereum contract.
type DexTransaferCallerRaw struct {
	Contract *DexTransaferCaller // Generic read-only contract binding to access the raw methods on
}

// DexTransaferTransactorRaw is an auto generated low-level write-only Go binding around an Ethereum contract.
type DexTransaferTransactorRaw struct {
	Contract *DexTransaferTransactor // Generic write-only contract binding to access the raw methods on
}

// NewDexTransafer creates a new instance of DexTransafer, bound to a specific deployed contract.
func NewDexTransafer(address common.Address, backend bind.ContractBackend) (*DexTransafer, error) {
	contract, err := bindDexTransafer(address, backend, backend, backend)
	if err != nil {
		return nil, err
	}
	return &DexTransafer{DexTransaferCaller: DexTransaferCaller{contract: contract}, DexTransaferTransactor: DexTransaferTransactor{contract: contract}, DexTransaferFilterer: DexTransaferFilterer{contract: contract}}, nil
}

// NewDexTransaferCaller creates a new read-only instance of DexTransafer, bound to a specific deployed contract.
func NewDexTransaferCaller(address common.Address, caller bind.ContractCaller) (*DexTransaferCaller, error) {
	contract, err := bindDexTransafer(address, caller, nil, nil)
	if err != nil {
		return nil, err
	}
	return &DexTransaferCaller{contract: contract}, nil
}

// NewDexTransaferTransactor creates a new write-only instance of DexTransafer, bound to a specific deployed contract.
func NewDexTransaferTransactor(address common.Address, transactor bind.ContractTransactor) (*DexTransaferTransactor, error) {
	contract, err := bindDexTransafer(address, nil, transactor, nil)
	if err != nil {
		return nil, err
	}
	return &DexTransaferTransactor{contract: contract}, nil
}

// NewDexTransaferFilterer creates a new log filterer instance of DexTransafer, bound to a specific deployed contract.
func NewDexTransaferFilterer(address common.Address, filterer bind.ContractFilterer) (*DexTransaferFilterer, error) {
	contract, err := bindDexTransafer(address, nil, nil, filterer)
	if err != nil {
		return nil, err
	}
	return &DexTransaferFilterer{contract: contract}, nil
}

// bindDexTransafer binds a generic wrapper to an already deployed contract.
func bindDexTransafer(address common.Address, caller bind.ContractCaller, transactor bind.ContractTransactor, filterer bind.ContractFilterer) (*bind.BoundContract, error) {
	parsed, err := DexTransaferMetaData.GetAbi()
	if err != nil {
		return nil, err
	}
	return bind.NewBoundContract(address, *parsed, caller, transactor, filterer), nil
}

// Call invokes the (constant) contract method with params as input values and
// sets the output to result. The result type might be a single field for simple
// returns, a slice of interfaces for anonymous returns and a struct for named
// returns.
func (_DexTransafer *DexTransaferRaw) Call(opts *bind.CallOpts, result *[]interface{}, method string, params ...interface{}) error {
	return _DexTransafer.Contract.DexTransaferCaller.contract.Call(opts, result, method, params...)
}

// Transfer initiates a plain transaction to move funds to the contract, calling
// its default method if one is available.
func (_DexTransafer *DexTransaferRaw) Transfer(opts *bind.TransactOpts) (*types.Transaction, error) {
	return _DexTransafer.Contract.DexTransaferTransactor.contract.Transfer(opts)
}

// Transact invokes the (paid) contract method with params as input values.
func (_DexTransafer *DexTransaferRaw) Transact(opts *bind.TransactOpts, method string, params ...interface{}) (*types.Transaction, error) {
	return _DexTransafer.Contract.DexTransaferTransactor.contract.Transact(opts, method, params...)
}

// Call invokes the (constant) contract method with params as input values and
// sets the output to result. The result type might be a single field for simple
// returns, a slice of interfaces for anonymous returns and a struct for named
// returns.
func (_DexTransafer *DexTransaferCallerRaw) Call(opts *bind.CallOpts, result *[]interface{}, method string, params ...interface{}) error {
	return _DexTransafer.Contract.contract.Call(opts, result, method, params...)
}

// Transfer initiates a plain transaction to move funds to the contract, calling
// its default method if one is available.
func (_DexTransafer *DexTransaferTransactorRaw) Transfer(opts *bind.TransactOpts) (*types.Transaction, error) {
	return _DexTransafer.Contract.contract.Transfer(opts)
}

// Transact invokes the (paid) contract method with params as input values.
func (_DexTransafer *DexTransaferTransactorRaw) Transact(opts *bind.TransactOpts, method string, params ...interface{}) (*types.Transaction, error) {
	return _DexTransafer.Contract.contract.Transact(opts, method, params...)
}

// Balances is a free data retrieval call binding the contract method 0x27e235e3.
//
// Solidity: function balances(address ) view returns(uint256)
func (_DexTransafer *DexTransaferCaller) Balances(opts *bind.CallOpts, arg0 common.Address) (*big.Int, error) {
	var out []interface{}
	err := _DexTransafer.contract.Call(opts, &out, "balances", arg0)

	if err != nil {
		return *new(*big.Int), err
	}

	out0 := *abi.ConvertType(out[0], new(*big.Int)).(**big.Int)

	return out0, err

}

// Balances is a free data retrieval call binding the contract method 0x27e235e3.
//
// Solidity: function balances(address ) view returns(uint256)
func (_DexTransafer *DexTransaferSession) Balances(arg0 common.Address) (*big.Int, error) {
	return _DexTransafer.Contract.Balances(&_DexTransafer.CallOpts, arg0)
}

// Balances is a free data retrieval call binding the contract method 0x27e235e3.
//
// Solidity: function balances(address ) view returns(uint256)
func (_DexTransafer *DexTransaferCallerSession) Balances(arg0 common.Address) (*big.Int, error) {
	return _DexTransafer.Contract.Balances(&_DexTransafer.CallOpts, arg0)
}

// Deposit is a paid mutator transaction binding the contract method 0xd0e30db0.
//
// Solidity: function deposit() payable returns()
func (_DexTransafer *DexTransaferTransactor) Deposit(opts *bind.TransactOpts) (*types.Transaction, error) {
	return _DexTransafer.contract.Transact(opts, "deposit")
}

// Deposit is a paid mutator transaction binding the contract method 0xd0e30db0.
//
// Solidity: function deposit() payable returns()
func (_DexTransafer *DexTransaferSession) Deposit() (*types.Transaction, error) {
	return _DexTransafer.Contract.Deposit(&_DexTransafer.TransactOpts)
}

// Deposit is a paid mutator transaction binding the contract method 0xd0e30db0.
//
// Solidity: function deposit() payable returns()
func (_DexTransafer *DexTransaferTransactorSession) Deposit() (*types.Transaction, error) {
	return _DexTransafer.Contract.Deposit(&_DexTransafer.TransactOpts)
}

// TransferOwnership is a paid mutator transaction binding the contract method 0xf2fde38b.
//
// Solidity: function transferOwnership(address newOwner) returns()
func (_DexTransafer *DexTransaferTransactor) TransferOwnership(opts *bind.TransactOpts, newOwner common.Address) (*types.Transaction, error) {
	return _DexTransafer.contract.Transact(opts, "transferOwnership", newOwner)
}

// TransferOwnership is a paid mutator transaction binding the contract method 0xf2fde38b.
//
// Solidity: function transferOwnership(address newOwner) returns()
func (_DexTransafer *DexTransaferSession) TransferOwnership(newOwner common.Address) (*types.Transaction, error) {
	return _DexTransafer.Contract.TransferOwnership(&_DexTransafer.TransactOpts, newOwner)
}

// TransferOwnership is a paid mutator transaction binding the contract method 0xf2fde38b.
//
// Solidity: function transferOwnership(address newOwner) returns()
func (_DexTransafer *DexTransaferTransactorSession) TransferOwnership(newOwner common.Address) (*types.Transaction, error) {
	return _DexTransafer.Contract.TransferOwnership(&_DexTransafer.TransactOpts, newOwner)
}

// TransferToken is a paid mutator transaction binding the contract method 0x9fc71b31.
//
// Solidity: function transferToken(uint256 amount) returns()
func (_DexTransafer *DexTransaferTransactor) TransferToken(opts *bind.TransactOpts, amount *big.Int) (*types.Transaction, error) {
	return _DexTransafer.contract.Transact(opts, "transferToken", amount)
}

// TransferToken is a paid mutator transaction binding the contract method 0x9fc71b31.
//
// Solidity: function transferToken(uint256 amount) returns()
func (_DexTransafer *DexTransaferSession) TransferToken(amount *big.Int) (*types.Transaction, error) {
	return _DexTransafer.Contract.TransferToken(&_DexTransafer.TransactOpts, amount)
}

// TransferToken is a paid mutator transaction binding the contract method 0x9fc71b31.
//
// Solidity: function transferToken(uint256 amount) returns()
func (_DexTransafer *DexTransaferTransactorSession) TransferToken(amount *big.Int) (*types.Transaction, error) {
	return _DexTransafer.Contract.TransferToken(&_DexTransafer.TransactOpts, amount)
}

// TransferoutToken is a paid mutator transaction binding the contract method 0x17a8382c.
//
// Solidity: function transferoutToken(address addr, uint256 amount) returns()
func (_DexTransafer *DexTransaferTransactor) TransferoutToken(opts *bind.TransactOpts, addr common.Address, amount *big.Int) (*types.Transaction, error) {
	return _DexTransafer.contract.Transact(opts, "transferoutToken", addr, amount)
}

// TransferoutToken is a paid mutator transaction binding the contract method 0x17a8382c.
//
// Solidity: function transferoutToken(address addr, uint256 amount) returns()
func (_DexTransafer *DexTransaferSession) TransferoutToken(addr common.Address, amount *big.Int) (*types.Transaction, error) {
	return _DexTransafer.Contract.TransferoutToken(&_DexTransafer.TransactOpts, addr, amount)
}

// TransferoutToken is a paid mutator transaction binding the contract method 0x17a8382c.
//
// Solidity: function transferoutToken(address addr, uint256 amount) returns()
func (_DexTransafer *DexTransaferTransactorSession) TransferoutToken(addr common.Address, amount *big.Int) (*types.Transaction, error) {
	return _DexTransafer.Contract.TransferoutToken(&_DexTransafer.TransactOpts, addr, amount)
}

// DexTransaferOwnershipTransferredIterator is returned from FilterOwnershipTransferred and is used to iterate over the raw logs and unpacked data for OwnershipTransferred events raised by the DexTransafer contract.
type DexTransaferOwnershipTransferredIterator struct {
	Event *DexTransaferOwnershipTransferred // Event containing the contract specifics and raw log

	contract *bind.BoundContract // Generic contract to use for unpacking event data
	event    string              // Event name to use for unpacking event data

	logs chan types.Log        // Log channel receiving the found contract events
	sub  ethereum.Subscription // Subscription for errors, completion and termination
	done bool                  // Whether the subscription completed delivering logs
	fail error                 // Occurred error to stop iteration
}

// Next advances the iterator to the subsequent event, returning whether there
// are any more events found. In case of a retrieval or parsing error, false is
// returned and Error() can be queried for the exact failure.
func (it *DexTransaferOwnershipTransferredIterator) Next() bool {
	// If the iterator failed, stop iterating
	if it.fail != nil {
		return false
	}
	// If the iterator completed, deliver directly whatever's available
	if it.done {
		select {
		case log := <-it.logs:
			it.Event = new(DexTransaferOwnershipTransferred)
			if err := it.contract.UnpackLog(it.Event, it.event, log); err != nil {
				it.fail = err
				return false
			}
			it.Event.Raw = log
			return true

		default:
			return false
		}
	}
	// Iterator still in progress, wait for either a data or an error event
	select {
	case log := <-it.logs:
		it.Event = new(DexTransaferOwnershipTransferred)
		if err := it.contract.UnpackLog(it.Event, it.event, log); err != nil {
			it.fail = err
			return false
		}
		it.Event.Raw = log
		return true

	case err := <-it.sub.Err():
		it.done = true
		it.fail = err
		return it.Next()
	}
}

// Error returns any retrieval or parsing error occurred during filtering.
func (it *DexTransaferOwnershipTransferredIterator) Error() error {
	return it.fail
}

// Close terminates the iteration process, releasing any pending underlying
// resources.
func (it *DexTransaferOwnershipTransferredIterator) Close() error {
	it.sub.Unsubscribe()
	return nil
}

// DexTransaferOwnershipTransferred represents a OwnershipTransferred event raised by the DexTransafer contract.
type DexTransaferOwnershipTransferred struct {
	OldOwner common.Address
	NewOwner common.Address
	Raw      types.Log // Blockchain specific contextual infos
}

// FilterOwnershipTransferred is a free log retrieval operation binding the contract event 0x8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0.
//
// Solidity: event OwnershipTransferred(address oldOwner, address newOwner)
func (_DexTransafer *DexTransaferFilterer) FilterOwnershipTransferred(opts *bind.FilterOpts) (*DexTransaferOwnershipTransferredIterator, error) {

	logs, sub, err := _DexTransafer.contract.FilterLogs(opts, "OwnershipTransferred")
	if err != nil {
		return nil, err
	}
	return &DexTransaferOwnershipTransferredIterator{contract: _DexTransafer.contract, event: "OwnershipTransferred", logs: logs, sub: sub}, nil
}

// WatchOwnershipTransferred is a free log subscription operation binding the contract event 0x8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0.
//
// Solidity: event OwnershipTransferred(address oldOwner, address newOwner)
func (_DexTransafer *DexTransaferFilterer) WatchOwnershipTransferred(opts *bind.WatchOpts, sink chan<- *DexTransaferOwnershipTransferred) (event.Subscription, error) {

	logs, sub, err := _DexTransafer.contract.WatchLogs(opts, "OwnershipTransferred")
	if err != nil {
		return nil, err
	}
	return event.NewSubscription(func(quit <-chan struct{}) error {
		defer sub.Unsubscribe()
		for {
			select {
			case log := <-logs:
				// New log arrived, parse the event and forward to the user
				event := new(DexTransaferOwnershipTransferred)
				if err := _DexTransafer.contract.UnpackLog(event, "OwnershipTransferred", log); err != nil {
					return err
				}
				event.Raw = log

				select {
				case sink <- event:
				case err := <-sub.Err():
					return err
				case <-quit:
					return nil
				}
			case err := <-sub.Err():
				return err
			case <-quit:
				return nil
			}
		}
	}), nil
}

// ParseOwnershipTransferred is a log parse operation binding the contract event 0x8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0.
//
// Solidity: event OwnershipTransferred(address oldOwner, address newOwner)
func (_DexTransafer *DexTransaferFilterer) ParseOwnershipTransferred(log types.Log) (*DexTransaferOwnershipTransferred, error) {
	event := new(DexTransaferOwnershipTransferred)
	if err := _DexTransafer.contract.UnpackLog(event, "OwnershipTransferred", log); err != nil {
		return nil, err
	}
	event.Raw = log
	return event, nil
}

// DexTransaferTransferTokenLogIterator is returned from FilterTransferTokenLog and is used to iterate over the raw logs and unpacked data for TransferTokenLog events raised by the DexTransafer contract.
type DexTransaferTransferTokenLogIterator struct {
	Event *DexTransaferTransferTokenLog // Event containing the contract specifics and raw log

	contract *bind.BoundContract // Generic contract to use for unpacking event data
	event    string              // Event name to use for unpacking event data

	logs chan types.Log        // Log channel receiving the found contract events
	sub  ethereum.Subscription // Subscription for errors, completion and termination
	done bool                  // Whether the subscription completed delivering logs
	fail error                 // Occurred error to stop iteration
}

// Next advances the iterator to the subsequent event, returning whether there
// are any more events found. In case of a retrieval or parsing error, false is
// returned and Error() can be queried for the exact failure.
func (it *DexTransaferTransferTokenLogIterator) Next() bool {
	// If the iterator failed, stop iterating
	if it.fail != nil {
		return false
	}
	// If the iterator completed, deliver directly whatever's available
	if it.done {
		select {
		case log := <-it.logs:
			it.Event = new(DexTransaferTransferTokenLog)
			if err := it.contract.UnpackLog(it.Event, it.event, log); err != nil {
				it.fail = err
				return false
			}
			it.Event.Raw = log
			return true

		default:
			return false
		}
	}
	// Iterator still in progress, wait for either a data or an error event
	select {
	case log := <-it.logs:
		it.Event = new(DexTransaferTransferTokenLog)
		if err := it.contract.UnpackLog(it.Event, it.event, log); err != nil {
			it.fail = err
			return false
		}
		it.Event.Raw = log
		return true

	case err := <-it.sub.Err():
		it.done = true
		it.fail = err
		return it.Next()
	}
}

// Error returns any retrieval or parsing error occurred during filtering.
func (it *DexTransaferTransferTokenLogIterator) Error() error {
	return it.fail
}

// Close terminates the iteration process, releasing any pending underlying
// resources.
func (it *DexTransaferTransferTokenLogIterator) Close() error {
	it.sub.Unsubscribe()
	return nil
}

// DexTransaferTransferTokenLog represents a TransferTokenLog event raised by the DexTransafer contract.
type DexTransaferTransferTokenLog struct {
	From   common.Address
	To     common.Address
	Amount *big.Int
	Raw    types.Log // Blockchain specific contextual infos
}

// FilterTransferTokenLog is a free log retrieval operation binding the contract event 0xf9d761a849dbc8dc9f0327764bf19102e26d5466881735c450b4fd28514471ca.
//
// Solidity: event transferTokenLog(address from, address to, uint256 amount)
func (_DexTransafer *DexTransaferFilterer) FilterTransferTokenLog(opts *bind.FilterOpts) (*DexTransaferTransferTokenLogIterator, error) {

	logs, sub, err := _DexTransafer.contract.FilterLogs(opts, "transferTokenLog")
	if err != nil {
		return nil, err
	}
	return &DexTransaferTransferTokenLogIterator{contract: _DexTransafer.contract, event: "transferTokenLog", logs: logs, sub: sub}, nil
}

// WatchTransferTokenLog is a free log subscription operation binding the contract event 0xf9d761a849dbc8dc9f0327764bf19102e26d5466881735c450b4fd28514471ca.
//
// Solidity: event transferTokenLog(address from, address to, uint256 amount)
func (_DexTransafer *DexTransaferFilterer) WatchTransferTokenLog(opts *bind.WatchOpts, sink chan<- *DexTransaferTransferTokenLog) (event.Subscription, error) {

	logs, sub, err := _DexTransafer.contract.WatchLogs(opts, "transferTokenLog")
	if err != nil {
		return nil, err
	}
	return event.NewSubscription(func(quit <-chan struct{}) error {
		defer sub.Unsubscribe()
		for {
			select {
			case log := <-logs:
				// New log arrived, parse the event and forward to the user
				event := new(DexTransaferTransferTokenLog)
				if err := _DexTransafer.contract.UnpackLog(event, "transferTokenLog", log); err != nil {
					return err
				}
				event.Raw = log

				select {
				case sink <- event:
				case err := <-sub.Err():
					return err
				case <-quit:
					return nil
				}
			case err := <-sub.Err():
				return err
			case <-quit:
				return nil
			}
		}
	}), nil
}

// ParseTransferTokenLog is a log parse operation binding the contract event 0xf9d761a849dbc8dc9f0327764bf19102e26d5466881735c450b4fd28514471ca.
//
// Solidity: event transferTokenLog(address from, address to, uint256 amount)
func (_DexTransafer *DexTransaferFilterer) ParseTransferTokenLog(log types.Log) (*DexTransaferTransferTokenLog, error) {
	event := new(DexTransaferTransferTokenLog)
	if err := _DexTransafer.contract.UnpackLog(event, "transferTokenLog", log); err != nil {
		return nil, err
	}
	event.Raw = log
	return event, nil
}

// DexTransaferTransferoutTokenLogIterator is returned from FilterTransferoutTokenLog and is used to iterate over the raw logs and unpacked data for TransferoutTokenLog events raised by the DexTransafer contract.
type DexTransaferTransferoutTokenLogIterator struct {
	Event *DexTransaferTransferoutTokenLog // Event containing the contract specifics and raw log

	contract *bind.BoundContract // Generic contract to use for unpacking event data
	event    string              // Event name to use for unpacking event data

	logs chan types.Log        // Log channel receiving the found contract events
	sub  ethereum.Subscription // Subscription for errors, completion and termination
	done bool                  // Whether the subscription completed delivering logs
	fail error                 // Occurred error to stop iteration
}

// Next advances the iterator to the subsequent event, returning whether there
// are any more events found. In case of a retrieval or parsing error, false is
// returned and Error() can be queried for the exact failure.
func (it *DexTransaferTransferoutTokenLogIterator) Next() bool {
	// If the iterator failed, stop iterating
	if it.fail != nil {
		return false
	}
	// If the iterator completed, deliver directly whatever's available
	if it.done {
		select {
		case log := <-it.logs:
			it.Event = new(DexTransaferTransferoutTokenLog)
			if err := it.contract.UnpackLog(it.Event, it.event, log); err != nil {
				it.fail = err
				return false
			}
			it.Event.Raw = log
			return true

		default:
			return false
		}
	}
	// Iterator still in progress, wait for either a data or an error event
	select {
	case log := <-it.logs:
		it.Event = new(DexTransaferTransferoutTokenLog)
		if err := it.contract.UnpackLog(it.Event, it.event, log); err != nil {
			it.fail = err
			return false
		}
		it.Event.Raw = log
		return true

	case err := <-it.sub.Err():
		it.done = true
		it.fail = err
		return it.Next()
	}
}

// Error returns any retrieval or parsing error occurred during filtering.
func (it *DexTransaferTransferoutTokenLogIterator) Error() error {
	return it.fail
}

// Close terminates the iteration process, releasing any pending underlying
// resources.
func (it *DexTransaferTransferoutTokenLogIterator) Close() error {
	it.sub.Unsubscribe()
	return nil
}

// DexTransaferTransferoutTokenLog represents a TransferoutTokenLog event raised by the DexTransafer contract.
type DexTransaferTransferoutTokenLog struct {
	Addr   common.Address
	Amount *big.Int
	Raw    types.Log // Blockchain specific contextual infos
}

// FilterTransferoutTokenLog is a free log retrieval operation binding the contract event 0x77e80aee6b887c753e78ac67aad733014bf83bce948264be076f4cbfb554de2b.
//
// Solidity: event transferoutTokenLog(address addr, uint256 amount)
func (_DexTransafer *DexTransaferFilterer) FilterTransferoutTokenLog(opts *bind.FilterOpts) (*DexTransaferTransferoutTokenLogIterator, error) {

	logs, sub, err := _DexTransafer.contract.FilterLogs(opts, "transferoutTokenLog")
	if err != nil {
		return nil, err
	}
	return &DexTransaferTransferoutTokenLogIterator{contract: _DexTransafer.contract, event: "transferoutTokenLog", logs: logs, sub: sub}, nil
}

// WatchTransferoutTokenLog is a free log subscription operation binding the contract event 0x77e80aee6b887c753e78ac67aad733014bf83bce948264be076f4cbfb554de2b.
//
// Solidity: event transferoutTokenLog(address addr, uint256 amount)
func (_DexTransafer *DexTransaferFilterer) WatchTransferoutTokenLog(opts *bind.WatchOpts, sink chan<- *DexTransaferTransferoutTokenLog) (event.Subscription, error) {

	logs, sub, err := _DexTransafer.contract.WatchLogs(opts, "transferoutTokenLog")
	if err != nil {
		return nil, err
	}
	return event.NewSubscription(func(quit <-chan struct{}) error {
		defer sub.Unsubscribe()
		for {
			select {
			case log := <-logs:
				// New log arrived, parse the event and forward to the user
				event := new(DexTransaferTransferoutTokenLog)
				if err := _DexTransafer.contract.UnpackLog(event, "transferoutTokenLog", log); err != nil {
					return err
				}
				event.Raw = log

				select {
				case sink <- event:
				case err := <-sub.Err():
					return err
				case <-quit:
					return nil
				}
			case err := <-sub.Err():
				return err
			case <-quit:
				return nil
			}
		}
	}), nil
}

// ParseTransferoutTokenLog is a log parse operation binding the contract event 0x77e80aee6b887c753e78ac67aad733014bf83bce948264be076f4cbfb554de2b.
//
// Solidity: event transferoutTokenLog(address addr, uint256 amount)
func (_DexTransafer *DexTransaferFilterer) ParseTransferoutTokenLog(log types.Log) (*DexTransaferTransferoutTokenLog, error) {
	event := new(DexTransaferTransferoutTokenLog)
	if err := _DexTransafer.contract.UnpackLog(event, "transferoutTokenLog", log); err != nil {
		return nil, err
	}
	event.Raw = log
	return event, nil
}

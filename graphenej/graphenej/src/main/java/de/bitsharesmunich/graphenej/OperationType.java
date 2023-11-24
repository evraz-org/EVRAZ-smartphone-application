package de.bitsharesmunich.graphenej;

/**
 * Enum type used to keep track of all the operation types and their corresponding ids.
 *
 * <a href="https://bitshares.org/doxygen/operations_8hpp_source.html">Source</a>
 *
 * Created by nelson on 11/6/16.
 */
public enum OperationType {
    TRANSFER_OPERATION,
    LIMIT_ORDER_CREATE_OPERATION,
    LIMIT_ORDER_CANCEL_OPERATION,
    CALL_ORDER_UPDATE_OPERATION,
    FILL_ORDER_OPERATION,           // VIRTUAL
    ACCOUNT_CREATE_OPERATION,
    ACCOUNT_UPDATE_OPERATION,
    ACCOUNT_WHITELIST_OPERATION,
    ACCOUNT_UPGRADE_OPERATION,
    ACCOUNT_TRANSFER_OPERATION,
    ASSET_CREATE_OPERATION,
    ASSET_UPDATE_OPERATION,
    ASSET_UPDATE_BITASSET_OPERATION,
    ASSET_UPDATE_FEED_PRODUCERS_OPERATION,
    ASSET_ISSUE_OPERATION,
    ASSET_RESERVE_OPERATION,
    ASSET_FUND_FEE_POOL_OPERATION,
    ASSET_SETTLE_OPERATION,
    ASSET_GLOBAL_SETTLE_OPERATION,
    ASSET_PUBLISH_FEED_OPERATION,
    WITNESS_CREATE_OPERATION,
    WITNESS_UPDATE_OPERATION,
    PROPOSAL_CREATE_OPERATION,
    PROPOSAL_UPDATE_OPERATION,
    PROPOSAL_DELETE_OPERATION,
    WITHDRAW_PERMISSION_CREATE_OPERATION,
    WITHDRAW_PERMISSION_UPDATE_OPERATION,
    WITHDRAW_PERMISSION_CLAIM_OPERATION,
    WITHDRAW_PERMISSION_DELETE_OPERATION,
    COMMITTEE_MEMBER_CREATE_OPERATION,
    COMMITTEE_MEMBER_UPDATE_OPERATION,
    COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION,
    VESTING_BALANCE_CREATE_OPERATION,
    VESTING_BALANCE_WITHDRAW_OPERATION,
    WORKER_CREATE_OPERATION,
    CUSTOM_OPERATION,
    ASSERT_OPERATION,
    BALANCE_CLAIM_OPERATION,
    OVERRIDE_TRANSFER_OPERATION,
    TRANSFER_TO_BLIND_OPERATION,
    BLIND_TRANSFER_OPERATION,
    TRANSFER_FROM_BLIND_OPERATION,
    ASSET_SETTLE_CANCEL_OPERATION,  // VIRTUAL
    ASSET_CLAIM_FEES_OPERATION
}

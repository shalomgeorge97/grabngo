package com.example.grabngo.util;


import java.util.ArrayList;
import java.util.List;

/**
 * Shared order status constants and the allowed next-status rules.
 * Used by: Manager Order Details (status Spinner, Accept/Reject),
 * Customer Order Details (cancel, status trail), Order History (badge colour via
 * StatusBadgeHelper).
 *
 * Agree any change to these strings with the whole team before coding against them -
 * the database stores exactly these values.
 */
public final class OrderStatus {

    public static final String PENDING = "Pending";
    public static final String ACCEPTED = "Accept";
    public static final String REJECTED = "Reject";
    public static final String READY = "Ready";
    public static final String PICKED_UP = "Picked Up";
    public static final String DELIVERED = "Delivered";
    public static final String CANCELLED = "Cancelled";

    public static final String FULFILLMENT_PICKUP = "PICKUP";
    public static final String FULFILLMENT_DELIVERY = "DELIVERY";

    private OrderStatus() { }

    /**
     * What the manager's status Spinner should offer, given the order's current
     * status and whether it is pickup or delivery. Returns an empty list once the
     * order has reached a final state (Picked Up, Delivered, Rejected, Cancelled) -
     * the caller should hide the Spinner in that case.
     */
    public static List<String> getAllowedNextStatuses(String currentStatus, String fulfillmentType) {
        List<String> next = new ArrayList<>();
        switch (currentStatus) {
            case PENDING:
                // Accept/Reject are handled by their own buttons, not this Spinner.
                next.add(ACCEPTED);
                next.add(REJECTED);
                break;
            case ACCEPTED:
                next.add(READY);
                break;
            case READY:
                if (FULFILLMENT_DELIVERY.equals(fulfillmentType)) {
                    next.add(DELIVERED);
                } else {
                    next.add(PICKED_UP);
                }
                break;
            // REJECTED, PICKED_UP, DELIVERED, CANCELLED: no further transitions
            default:
                break;
        }
        return next;
    }

    /** True once an order can no longer change status (used to hide actions in the UI). */
    public static boolean isFinal(String status) {
        return REJECTED.equals(status) || PICKED_UP.equals(status)
                || DELIVERED.equals(status) || CANCELLED.equals(status);
    }

    /** Customers may only cancel while the order is still Pending. */
    public static boolean canCustomerCancel(String status) {
        return PENDING.equals(status);
    }

    /** The manager may only accept or reject while the order is still Pending. */
    public static boolean canManagerAcceptOrReject(String status) {
        return PENDING.equals(status);
    }
}

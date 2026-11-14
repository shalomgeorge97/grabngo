package com.example.grabngo.util;

import android.widget.TextView;
import com.example.grabngo.R;

/**
 * Shared by both order details screens (and order history rows) to style the
 * status badge consistently. Keep this as the one place that maps a status
 * string to its colour, so the whole team shows the same colours everywhere.
 */
public final class StatusBadgeHelper {

    private StatusBadgeHelper() { }

    /** @param status one of: Pending, Accepted, Ready, Picked Up, Delivered, Rejected, Cancelled */
    public static void apply(TextView badge, String status) {
        badge.setText(status);
        int textColor;
        int bgColor;

        switch (status) {
            case "Accepted":
                textColor = R.color.status_accepted;
                bgColor = R.color.status_accepted_bg;
                break;
            case "Ready":
                textColor = R.color.status_ready;
                bgColor = R.color.status_ready_bg;
                break;
            case "Picked Up":
            case "Delivered":
                textColor = R.color.status_done;
                bgColor = R.color.status_done_bg;
                break;
            case "Rejected":
            case "Cancelled":
                textColor = R.color.status_rejected;
                bgColor = R.color.status_rejected_bg;
                break;
            case "Pending":
            default:
                textColor = R.color.status_pending;
                bgColor = R.color.status_pending_bg;
                break;
        }

        badge.setTextColor(badge.getContext().getColor(textColor));
        badge.getBackground().setTint(badge.getContext().getColor(bgColor));
    }
}

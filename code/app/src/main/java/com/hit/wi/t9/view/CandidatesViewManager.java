package com.hit.wi.t9.view;

import android.view.View;

import com.hit.wi.t9.T9SoftKeyboard8;

public interface CandidatesViewManager {
    /**
     * Initialize the candidates view.
     *
     * @param parent The T9SoftKeyboard object
     * @return The candidates view created in the initialize process; {@code null} if cannot create a candidates view.
     */
    View initView(T9SoftKeyboard8 parent);

    /**
     * Display candidates.
     */
    void displayCandidates(String type);

    /**
     * Clear and hide the candidates view.
     */
    void clearCandidates();
}


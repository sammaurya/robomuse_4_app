package com.sammaurya.robomuse.Core;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Interface for objects that can be loaded and saved with Bundles.
 */
public interface Savable {

    /**
     * Load from a Bundle.
     * @param bundle The Bundle
     */
    void load(@NonNull Bundle bundle);

    /**
     * Save to a Bundle.
     * @param bundle The Bundle
     */
    void save(@NonNull Bundle bundle);

}
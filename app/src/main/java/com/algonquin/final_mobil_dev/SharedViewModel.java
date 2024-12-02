package com.algonquin.final_mobil_dev;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * SharedViewModel is a ViewModel class that holds and manages UI-related data in a lifecycle-conscious way.
 * It allows data to survive configuration changes such as screen rotations.
 */
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> updated = new MutableLiveData<>();

    /**
     * Sets the updated status.
     *
     * @param isSaved The new updated status.
     */
    public void setUpdated(boolean isSaved) {
        updated.postValue(isSaved);
    }

    /**
     * Gets the updated status as LiveData.
     *
     * @return A LiveData object containing the updated status.
     */
    public LiveData<Boolean> getUpdated() {
        return updated;
    }
}
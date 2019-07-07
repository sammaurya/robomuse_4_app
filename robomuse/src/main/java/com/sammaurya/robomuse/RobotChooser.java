package com.sammaurya.robomuse;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sammaurya.robomuse.Core.RobotInfo;
import com.sammaurya.robomuse.Core.RobotInfoAdapter;
import com.sammaurya.robomuse.Core.RobotStorage;
import com.sammaurya.robomuse.Fragments.AddEditRobotDialogFragment;
import com.sammaurya.robomuse.Fragments.ConfirmDeleteDialogFragment;

public class RobotChooser extends AppCompatActivity implements AddEditRobotDialogFragment.DialogListener, ConfirmDeleteDialogFragment.DialogListener {

    /** Key for whether this is the first time the app has been launched */
    // public static final String FIRST_TIME_LAUNCH_KEY = "FIRST_TIME_LAUNCH";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    // private ShowcaseView showcaseView;


    // Log tag String
    private static final String TAG = "RobotChooser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_robot_chooser);

        mRecyclerView = findViewById(R.id.robot_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVisibility(View.VISIBLE);

        RobotStorage.load(this);

        // Adapter for creating the list of Robot options
        mAdapter = new RobotInfoAdapter(this, RobotStorage.getRobots());

        mRecyclerView.setAdapter(mAdapter);

      //  ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_robot_chooser, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Callback for when the user wants to add a new RobotItem.
     * @param item The selected MenuItem
     * @return True if the item selection was handled and false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_robot:
                Toast.makeText(this, "Add robot dialog", Toast.LENGTH_SHORT).show();

                RobotInfo.resolveRobotCount(RobotStorage.getRobots());

                AddEditRobotDialogFragment addRobotDialogFragment = new AddEditRobotDialogFragment();
                addRobotDialogFragment.setArguments(null);
                addRobotDialogFragment.show(getSupportFragmentManager(), "addrobotdialog");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAddEditDialogPositiveClick(RobotInfo newRobotInfo, int position) {
        if (position >= 0 && position < RobotStorage.getRobots().size()) {
            updateRobot(position, newRobotInfo);
        } else {
            addRobot(newRobotInfo);
        }
    }


    @Override
    public void onAddEditDialogNegativeClick(DialogFragment dialog) {

    }
    @Override
    public void onConfirmDeleteDialogPositiveClick(int position, String name) {
        if (position >= 0 && position < RobotStorage.getRobots().size()) {
            removeRobot(position);
        }
    }

    @Override
    public void onConfirmDeleteDialogNegativeClick() {

    }

    /**
     * Adds a new RobotInfo.
     *
     * @param info The new RobotInfo
     * @return True if the RobotInfo was added successfully, false otherwise
     */
    public boolean addRobot(RobotInfo info) {
        RobotStorage.add(this, info);

        mAdapter.notifyItemInserted(RobotStorage.getRobots().size() - 1);

        return true;
    }

    /**
     * Updates the RobotInfo at the specified position.
     *
     * @param position     The position of the RobotInfo to update
     * @param newRobotInfo The updated RobotInfo
     */
    public void updateRobot(int position, RobotInfo newRobotInfo) {

        Log.d(TAG, "updateRobot at position " + position + ": " + newRobotInfo);

        RobotStorage.update(this, newRobotInfo);
        mAdapter.notifyItemChanged(position);
    }

    /**
     * Removes the RobotInfo at the specified position.
     *
     * @param position The position of the RobotInfo to remove
     * @return The removed RobotInfo if it existed
     */
    public RobotInfo removeRobot(int position) {
        RobotInfo removed = RobotStorage.remove(this, position);

        if (removed != null) {
            mAdapter.notifyItemRemoved(position);
        }

        if (RobotStorage.getRobots().size() == 0) {
            mAdapter.notifyDataSetChanged();
        }

        return removed;
    }

    /**
     * @return mAdapter item count.
     */
    @SuppressWarnings("unused")
    int getAdapterSize() {
        return mAdapter.getItemCount();
    }

}

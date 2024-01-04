package com.example.proj_moneymanager.activities.Setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.databinding.FragmentMoreBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MoreFragment extends Fragment {
    FragmentMoreBinding binding;
    ListView lv_moreOption;
    ArrayList<Setting_Option> arr_moreOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Xử lý Setting Adapter cho listview
        lv_moreOption = binding.lvOptMore;
        arr_moreOption = new ArrayList<>();

        arr_moreOption.add(new Setting_Option("Instruction", R.drawable.ic_book));
        arr_moreOption.add(new Setting_Option("Feedback/Contact", R.drawable.ic_contact));

        SettingAdapter settingAdapter = new SettingAdapter(
                getActivity(),
                arr_moreOption
        );
        lv_moreOption.setAdapter(settingAdapter);

        lv_moreOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy ra mục được chọn từ Adapter
                Setting_Option selectedOption = arr_moreOption.get(position);

                // Xử lý sự kiện tùy thuộc vào mục được chọn
                if (selectedOption.getLabel().equals("Feedback/Contact")) {
                    // Mở dialog tương ứng với mục "Feedback/Contact"
                    new FeedbackTask(MoreFragment.this, "").execute();
                } else if (selectedOption.getLabel().equals("Instruction")) {
                    // Handle Instruction click
                }
                // Thêm các điều kiện khác nếu cần thiết
            }
        });

        return view;
    }

    private void showDialogForFeedback() {
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_feedbackcontact, null);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Feedback/Contact");

        CardView cardView1 = dialogView.findViewById(R.id.cardView1);
        CardView cardView2 = dialogView.findViewById(R.id.cardView2);
        CardView cardView3 = dialogView.findViewById(R.id.cardView3);

        // Set OnClickListener for each CardView
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/philinh.huynh.90");
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/adrian.jeidt");
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/hoaiphuongdg26");
            }
        });

        // Set up the OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the OK button click
                // ...
                dialog.dismiss(); // Close the dialog if needed
            }
        });

        // Display the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private static class FeedbackTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<MoreFragment> fragmentRef;
        private String url;

        FeedbackTask(MoreFragment fragment, String url) {
            fragmentRef = new WeakReference<>(fragment);
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background tasks here, if any
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Access the fragment through the WeakReference
            MoreFragment fragment = fragmentRef.get();
            if (fragment != null) {
                // Perform UI-related tasks
                fragment.showDialogForFeedback();
            }
        }
    }
}

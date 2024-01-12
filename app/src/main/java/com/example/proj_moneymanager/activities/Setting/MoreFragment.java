package com.example.proj_moneymanager.activities.Setting;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Instruction.Instruction;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.FragmentMoreBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MoreFragment extends Fragment {
    FragmentMoreBinding binding;
    ListView lv_moreOption;
    ArrayList<Setting_Option> arr_moreOption;
    int UserID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Xử lý Setting Adapter cho listview
        lv_moreOption = binding.lvOptMore;
        arr_moreOption = new ArrayList<>();

        arr_moreOption.add(new Setting_Option(getString(R.string.Instruction), R.drawable.icon_book));
        arr_moreOption.add(new Setting_Option(getString(R.string.Feedback_Contact), R.drawable.icon_contact));

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
                if (selectedOption.getLabel().equals(getString(R.string.Feedback_Contact))) {
                    // Mở dialog tương ứng với mục "Feedback/Contact"
                    new FeedbackTask(MoreFragment.this, "").execute();
                } else if (selectedOption.getLabel().equals(getString(R.string.Instruction))) {
                    SharedPreferences preferences = getContext().getSharedPreferences("MyPreferencesInstruction", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("hasViewedInstruction", false);
                    editor.apply();

                    Intent intent = new Intent(getActivity(), Instruction.class);
                    startActivity(intent);
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
        builder.setView(dialogView).setTitle("Feedback/Contact");

        CardView cardView1 = dialogView.findViewById(R.id.cardView1);
        CardView cardView2 = dialogView.findViewById(R.id.cardView2);
        CardView cardView3 = dialogView.findViewById(R.id.cardView3);
        CardView cardView_mail = dialogView.findViewById(R.id.cardView_mail);

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
        // Set OnClickListener for cardView_mail
        cardView_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMailDialog();
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
    private void showMailDialog() {
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_mail, null);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView); // Set the view to the builder

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            // Cấu hình Dialog để hiển thị full screen và mờ đằng sau
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 1f;
            window.setAttributes(params);
        }

        Spinner mSpinnerTo;
        EditText mEditTextSubject;
        EditText mEditTextMessage;

        mSpinnerTo = dialogView.findViewById(R.id.spinner_to);
        String[] recipientOptions = {"21520323@gm.uit.edu.vn", "21520409@gm.uit.edu.vn", "21520795@gm.uit.edu.vn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, recipientOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTo.setAdapter(adapter);

        mEditTextSubject = dialogView.findViewById(R.id.edittext_subject);
        try {
            DbHelper dbHelper = new DbHelper(getContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            UserID = dbHelper.getUserID();
            dbHelper.close();
        } catch (Exception e){
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mEditTextSubject.setText("[UserID#" + UserID + "] FEEDBACK FROM MONEYMANAGER APP");

        mEditTextMessage = dialogView.findViewById(R.id.edittext_message);

        Button buttonSend = dialogView.findViewById(R.id.button_send);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientList = mSpinnerTo.getSelectedItem().toString();
                String[] recipients = recipientList.split(",");

                String subject = mEditTextSubject.getText().toString();
                String message = mEditTextMessage.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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

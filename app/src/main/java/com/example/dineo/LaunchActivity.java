public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button btnCustomer = findViewById(R.id.btnCustomer);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        btnCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, CustomerLoginActivity.class);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}

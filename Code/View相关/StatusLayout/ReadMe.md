```java
mStatusLayoutManager = StatusLayoutManager.newBuilder(this)
            .contentView(getContentView())
            .emptyDataView(R.layout.activity_emptydata)
            .errorView(R.layout.activity_error)
            .loadingView(R.layout.activity_loading)
            .netWorkErrorView(R.layout.activity_networkerror)
            .retryViewId(R.id.button_try)
            .onShowHideViewListener(new OnShowHideViewListener() {
                @Override
                public void onShowView(View view, int id) {
                }

                @Override
                public void onHideView(View view, int id) {
                }
            }).onRetryListener(new OnRetryListener() {
                @Override
                public void onRetry() {
                    mStatusLayoutManager.showLoading();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusLayoutManager.showContent();
                                }
                            });
                        }
                    }).start();

                }
            }).build();
// Fragment中
 return mStatusLayoutManager.getRootLayout();
// Activity中
// setContentView(mStatusLayoutManager.getRootLayout());
// mainLinearLayout.addView(mStatusLayoutManager.getRootLayout());
mStatusLayoutManager.showLoading();
```


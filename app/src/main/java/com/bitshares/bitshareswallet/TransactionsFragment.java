package com.bitshares.bitshareswallet;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitshares.bitshareswallet.viewmodel.TransactionViewModel;

import org.evrazcoin.evrazwallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionsFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TranactionsAdapter mTranactionsAdapter;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.layoutChartLoading) View mLoadingChartView;
    @BindView(R.id.layoutLoadingError) View mLoadingErrorView;

    private static final int SHOW_HISTORY_VIEW_LODING = 1;
    private static final int SHOW_HISTORY_VIEW_LODING_FAIL = 2;
    private static final int SHOW_HISTORY_VIEW_READY = 3;

    @Override
    public void onShow() {
        super.onShow();

        TransactionViewModel viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        viewModel.getOperationHistory().observe(
                this,
                operationHistoryWrapperResource -> {
                    switch (operationHistoryWrapperResource.status) {
                        case ERROR:
                            changeShowedView(SHOW_HISTORY_VIEW_LODING_FAIL);
                            break;
                        case SUCCESS:
                            changeShowedView(SHOW_HISTORY_VIEW_READY);
                            mTranactionsAdapter.notifyTransactionDataChanged(operationHistoryWrapperResource.data);
                            break;
                        case LOADING:
                            if (operationHistoryWrapperResource.data != null) {
                                changeShowedView(SHOW_HISTORY_VIEW_READY);
                                mTranactionsAdapter.notifyTransactionDataChanged(operationHistoryWrapperResource.data);
                            } else {
                                changeShowedView(SHOW_HISTORY_VIEW_LODING);
                            }
                            break;
                    }
                });
    }

    public TransactionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance(String param1, String param2) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTranactionsAdapter = new TranactionsAdapter(this, getActivity());
        mRecyclerView.setAdapter(mTranactionsAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void notifyUpdate(){

    }

    private void changeShowedView(int nShowView) {
        switch (nShowView) {
            case SHOW_HISTORY_VIEW_LODING:
                mLoadingChartView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mLoadingErrorView.setVisibility(View.INVISIBLE);
                break;
            case SHOW_HISTORY_VIEW_LODING_FAIL:
                mLoadingChartView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mLoadingErrorView.setVisibility(View.VISIBLE);
                break;
            case SHOW_HISTORY_VIEW_READY:
                mLoadingChartView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mLoadingErrorView.setVisibility(View.INVISIBLE);
                break;
        }
    }
}

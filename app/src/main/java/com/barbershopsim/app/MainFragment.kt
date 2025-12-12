package com.barbershopsim.app

import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.barbershopsim.app.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    private var binding: FragmentMainBinding? = null

    private var viewModel: MainViewModel? = null

    private var waitingCustomerViews: List<TextView>? = null
    private var barberViews: List<TextView>? = null
    private var barberCustomerViews: List<TextView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, NewInstanceFactory())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = viewModel!!
        val binding = binding!!

        waitingCustomerViews = listOf(
            binding.waitingCustomerOne.root,
            binding.waitingCustomerTwo.root,
            binding.waitingCustomerThree.root,
            binding.waitingCustomerFour.root
        )
        barberViews = listOf(
            binding.barberOne.root, binding.barberTwo.root, binding.barberThree.root, binding.barberFour.root
        )
        barberCustomerViews = listOf(
            binding.barberCustomerOne.root,
            binding.barberCustomerTwo.root,
            binding.barberCustomerThree.root,
            binding.barberCustomerFour.root
        )

        binding.shopName.text = viewModel.shopName
        binding.timescaleFactor.text = viewModel.timescaleFactor

        viewModel.isShopOpen.observe(viewLifecycleOwner, Observer { isShopOpen ->
            if (isShopOpen == true) {
                binding.closedSign.visibility = View.INVISIBLE
                binding.openSign.visibility = View.VISIBLE
            } else {
                binding.closedSign.visibility = View.VISIBLE
                binding.openSign.visibility = View.INVISIBLE
            }
        })
        viewModel.customerEnter.observe(viewLifecycleOwner, Observer { customer ->
            customer?.let { binding.incomingCustomer.root.text = it.toString().padStart(2, '0') }
                ?: run { binding.incomingCustomer.root.text = "" }
        })
        viewModel.customerExit.observe(viewLifecycleOwner, Observer { customer: Int? ->
            customer?.let { binding.outgoingCustomer.root.text = it.toString().padStart(2, '0') }
                ?: run { binding.outgoingCustomer.root.text = "" }
        })
        viewModel.waitingArea.observe(
            viewLifecycleOwner, Observer(this::updateWaitingArea)
        )
        viewModel.haircutArea.observe(
            viewLifecycleOwner, Observer(this::updateBarberChairs)
        )
        viewModel.clock.observe(viewLifecycleOwner, Observer { time -> binding.clock.text = time })

        binding.restartButton.setOnClickListener { viewModel.start() }
        binding.playPauseButton.setOnClickListener {
            // TODO Pause
            viewModel.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        barberCustomerViews = null
        barberViews = null
        waitingCustomerViews = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel = null
    }

    private fun updateWaitingArea(customers: List<Int>) {
        for (waitingCustomerView in waitingCustomerViews!!) {
            waitingCustomerView.text = ""
        }
        for (i in customers.indices) {
            val customer = customers[i]
            waitingCustomerViews!![i].text = customer.toString().padStart(2, '0')
        }
    }

    private fun updateBarberChairs(chairs: List<Pair<String?, Int>>) {
        for (barberView in barberViews!!) {
            barberView.text = ""
        }
        for (barberCustomerView in barberCustomerViews!!) {
            barberCustomerView.text = ""
        }
        for (i in chairs.indices) {
            val chair = chairs[i]
            chair.first?.let { barberViews!![i].text = it }
            if (chair.second != -1) {
                barberCustomerViews!![i].text = chair.second.toString().padStart(2, '0')
            }
        }
    }
}
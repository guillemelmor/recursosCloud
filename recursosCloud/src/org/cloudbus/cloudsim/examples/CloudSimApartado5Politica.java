package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

public class CloudSimApartado5Politica extends VmAllocationPolicy{
	
	private static boolean first_move = true;
	private static int id_host_i = 0;
	private static LinkedList <Integer> id_host = new LinkedList <Integer> ();
	private static int max = 0;
	private static int min = 0;
	
	private Map<String, Host> vmTable;

	private Map<String, Integer> usedPes;

	private List<Integer> freePes;

	public CloudSimApartado5Politica(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		for (Host host : getHostList()) {
			getFreePes().add(host.getNumberOfPes());

		}

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
		
		max = getHostList().size();
	}

	@Override
	public boolean allocateHostForVm(Vm vm) {
		int requiredPes = vm.getNumberOfPes();
		boolean result = false;
		int tries = 0;
		List<Integer> freePesTmp = new ArrayList<Integer>();
		for (Integer freePes : getFreePes()) {
			freePesTmp.add(freePes);
		}

		if (!getVmTable().containsKey(vm.getUid())) {
			do {
				int idx = -1;
				
				if(first_move == true){
					first_move = false;
					id_host_i = (int)(Math.random() * max) + min;
					id_host.add(id_host_i);
					idx = id_host_i;					
				}else{
					while (exist() == true){
						id_host_i = (int)(Math.random() * max) + min;
					}
					id_host.add(id_host_i);
					idx = id_host_i;
				}	
												
				Host host = getHostList().get(idx);
				result = host.vmCreate(vm);
				
				if (result) {
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					result = true;
					
					// reiniciamos las variables
					reset ();
					
					break;
				}
				tries++;
			} while (!result && tries < (max));
		}
		
		// reiniciamos las variables
		reset ();
		
		return result;
	}

	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
		int idx = getHostList().indexOf(host);
		int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
		}
	}

	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}

	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	

	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		return null;
	}

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			int requiredPes = vm.getNumberOfPes();
			int idx = getHostList().indexOf(host);
			getUsedPes().put(vm.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			Log.formatLine(
					"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
					CloudSim.clock());
			return true;
		}
		return false;
	}
	
	// comprueba que el numero de id no se haya comprobado ya
	public boolean exist (){
		for (int i = 0; i < id_host.size(); i++){
			if (id_host_i == id_host.get(i)){
				return true;
			}
		}		
		return false;
	}
	
	// reinicia las variables a su valor inicial
	public void reset (){
		first_move = true;
		id_host.clear();
	}
	
	// -------- setters y getters ----------
	public Map<String, Host> getVmTable() {
		return vmTable;
	}

	protected void setVmTable(Map<String, Host> vmTable) {
		this.vmTable = vmTable;
	}

	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	protected List<Integer> getFreePes() {
		return freePes;
	}

	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
	}
}

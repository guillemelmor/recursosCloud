package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class CloudSimApartado3 {
	private static List<Vm> vmlist;
	private static List<Cloudlet> cloudletList;
	private static int numCPUS = 0;
	private static int numcpus = 0;
	public static void main (String [] args){
		try{
			int numUsuarios = 10;
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;
			CloudSim.init (numUsuarios, calendar, trace_flag);
			
			// datacenter
			Datacenter datacenter = createDatacenter("Datacenter");	
			
			// lista de brokers
			LinkedList <DatacenterBroker> lista_brokers = new LinkedList <DatacenterBroker> ();
			
			for (int j = 0; j < numUsuarios; j++){
				// broker
				DatacenterBroker broker = new DatacenterBroker("Broker_" + j);
				int uid = broker.getId();
			
				// --------------------------------------------------------------------------
				
				// cloudlet
				Cloudlet cloudlet = null;
				List<Cloudlet> listaCloudlets = new ArrayList<Cloudlet> ();
				
				// caracteristicas del cloudlet
				UtilizationModel utilizationModel = new UtilizationModelFull ();
				int numCPUsCloudlet = 1;
				
				// constructor cloudlet
				for (int i = 0; i < 15; i++){
					cloudlet = new Cloudlet (i, 45000, numCPUsCloudlet, 1000000, 1500000, utilizationModel, utilizationModel, utilizationModel);
					cloudlet.setUserId (uid);
					listaCloudlets.add(cloudlet);
				}
				
				// -------------------------------------------------------------------------
				
				// maquina virtual
				Vm vm = null;
				List<Vm> listaVMs = new ArrayList<Vm> ();
				
				// caracteristicas de la maquina virtual
				int mips = 1200;
				int numCPUsVm = 1;
				numcpus += 2;
				int ram = 4096;
				long anchoBanda = 1000;
				long almacenamiento = 20000;
				String vmm = "Xen";
				
				// constructor maquina virtual
				vm = new Vm (0, uid, mips, numCPUsVm, ram, anchoBanda, almacenamiento, vmm, new CloudletSchedulerSpaceShared ());
				listaVMs.add(vm);
				vm = new Vm (1, uid, mips, numCPUsVm, ram, anchoBanda, almacenamiento, vmm, new CloudletSchedulerSpaceShared ());
				listaVMs.add(vm);
				vm = new Vm (2, uid, mips, numCPUsVm, ram, anchoBanda, almacenamiento, vmm, new CloudletSchedulerSpaceShared ());
				listaVMs.add(vm);
			
				// --------------------------------------------------------------------------
						
				broker.submitVmList (listaVMs);
				broker.submitCloudletList (listaCloudlets);
				lista_brokers.add(broker);
			}
			
			// --------------------------------------------------------------------------

			CloudSim.startSimulation ();
			CloudSim.stopSimulation();
			int id_user = 0;
			System.out.println("total de CPUs: " + numCPUS);
			System.out.println("total de cpus: " + numcpus);
			for(DatacenterBroker broker : lista_brokers){
				System.out.println("\n------ user_" + id_user + " ------");
				List<Cloudlet> newList = broker.getCloudletReceivedList ();
				printCloudletList(newList);			
				id_user++;
			}
				
		}catch (Exception e){}
	}
	
	// Centro de Datos
	private static Datacenter createDatacenter (String name){	
		// ---------------------------------------------------------------------------------
		
		// procesador
		Pe cpu = null;
		List<Pe> listaCPUs = new ArrayList <Pe> ();
		
		// caracteristicas del procesador
		int mips = 1200;
		
		// constructor procesador
		cpu = new Pe (0, new PeProvisionerSimple (mips));
		listaCPUs.add(cpu);
		numCPUS++;
		cpu = new Pe (1, new PeProvisionerSimple (mips));
		listaCPUs.add(cpu);
		numCPUS++;
		cpu = new Pe (2, new PeProvisionerSimple (mips));
		listaCPUs.add(cpu);
		numCPUS++;
		cpu = new Pe (3, new PeProvisionerSimple (mips));
		listaCPUs.add(cpu);
		numCPUS++;
		// ---------------------------------------------------------------------------------
		
		// host
		Host host = null;
		List<Host> listaHosts = new ArrayList<Host> ();
		
		// características del host
		int ram = 24576; //MB
		long anchoBanda = 10000; // Gbps
		long almacenamiento = 2000000; //MB
		
		// constructor host
		//host = new Host (0, new RamProvisionerSimple (ram), new BwProvisionerSimple (anchoBanda), almacenamiento, listaCPUs, new VmSchedulerTimeShared (listaCPUs));
		host = new Host (0, new RamProvisionerSimple (ram), new BwProvisionerSimple (anchoBanda), almacenamiento, listaCPUs, new VmSchedulerSpaceShared (listaCPUs));
		listaHosts.add(host);		
		host = new Host (1, new RamProvisionerSimple (ram), new BwProvisionerSimple (anchoBanda), almacenamiento, listaCPUs, new VmSchedulerSpaceShared (listaCPUs));
		listaHosts.add(host);
		host = new Host (2, new RamProvisionerSimple (ram), new BwProvisionerSimple (anchoBanda), almacenamiento, listaCPUs, new VmSchedulerSpaceShared (listaCPUs));
		listaHosts.add(host);
		host = new Host (3, new RamProvisionerSimple (ram), new BwProvisionerSimple (anchoBanda), almacenamiento, listaCPUs, new VmSchedulerSpaceShared (listaCPUs));
		listaHosts.add(host);
		host = new Host (4, new RamProvisionerSimple (ram), new BwProvisionerSimple (anchoBanda), almacenamiento, listaCPUs, new VmSchedulerSpaceShared (listaCPUs));
		listaHosts.add(host);
		
		// ---------------------------------------------------------------------------------
		
		// centro de datos
		Datacenter datacenter = null;
		
		// características del centro de datos
		String nombre = "Centro_de_Datos";
		String arquitectura = "x86";
		String so = "Linux";
		String vmm = "Xen";
		double zonaHoraria = 2.0;
		double costePorSeg = 0.01;
		double costePorMem = 0.005;
		double costePorAlm = 0.003;
		double costePorBw = 0.005;
		DatacenterCharacteristics caracteristicas = new DatacenterCharacteristics (arquitectura, so, vmm, listaHosts, zonaHoraria, costePorSeg, costePorMem, costePorAlm, costePorBw);
		
		// constructor centro de datos
		try{
			datacenter = new Datacenter (nombre, caracteristicas, new VmAllocationPolicySimple(listaHosts), new LinkedList <Storage> (), 0);
		}catch (Exception e) {}
		return datacenter;
	}
		
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}

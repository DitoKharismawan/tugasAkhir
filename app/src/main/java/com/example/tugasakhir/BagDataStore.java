package com.example.tugasakhir;

import com.example.tugasakhir.models.ArrayHashModel;

import java.util.ArrayList;
import java.util.HashMap;

public class BagDataStore {

    private HashMap<String, ArrayList<String>> bagsHolder = new HashMap<>();
    public GlobalUtils utils = new GlobalUtils();

    public HashMap<String, ArrayList<String>> getBagsHolder() {
        return bagsHolder;
    }

    // base function
    public HashMap<String, ArrayList<String>> setBagsHolder(HashMap<String, ArrayList<String>> value) {
        bagsHolder = value;
        return this.bagsHolder;
    }

    // New function to find the bag containing a connote
    public String getConnoteBag(String connoteId) {
        for (String bagName : bagsHolder.keySet()) {
            ArrayList<String> connotes = bagsHolder.get(bagName);
            if (connotes.contains(connoteId)) {
                return bagName;
            }
        }
        return null;
    }

    public void appendBag(String bagId) {
        bagsHolder.put(bagId, new ArrayList<>());
    }

    public void appendConnote(String bagId, String connoteId) {
        bagsHolder.get(bagId).add(connoteId);
    }


    /**
     * Menghapus connote dengan melakukan pencarian ke setiap BAG
     *
     * @param connoteId
     * @return true, apabila connote berhasil dihapus. selainnya false
     */
    public boolean smartRemoveConnote(String connoteId) {
        // Find the bag containing the connote using smartFindConnote
        String bagName = getConnoteBag(connoteId);

        if (bagName != null) {
            ArrayList<String> connotes = bagsHolder.get(bagName);
            connotes.remove(connoteId);

            // Check if the bag becomes empty after removal
            if (connotes.isEmpty()) {
                bagsHolder.remove(bagName);
            }

//            System.out.println("Connote " + connoteId + " removed from bag " + bagName);
            return true;
        } else {
//            System.out.println("Connote " + connoteId + " not found in any bag.");
            return false;
        }
    }

    public boolean moveConnote(String connoteId, String toBagId) {
        // Check if bagsHolder is initialized
        if (bagsHolder == null) {
            return false; // Return false if bagsHolder is not initialized
        }

        // Find the bag containing the connote using getConnoteBag
        String fromBagId = getConnoteBag(connoteId);

        // Connote not found in any bag
        if (fromBagId == null) {
            return false;
        }

        // Remove connote from the 'from' bag
        ArrayList<String> connoteList = bagsHolder.get(fromBagId);
        connoteList.remove(connoteId);

        // Check if the 'to' bag exists (optional)
        if (!bagsHolder.containsKey(toBagId)) {
            bagsHolder.put(toBagId, new ArrayList<String>()); // Create new bag if it doesn't exist
        }

        // Get the 'to' bag
        ArrayList<String> destinationList = bagsHolder.get(toBagId);

        // Add the connote to the 'to' bag
        destinationList.add(connoteId);

        return true; // Return true since connote was found and moved
    }

//    public HashMap<String, Integer> getConnoteCountsPerBag() {
//        HashMap<String, Integer> connoteCounts = new HashMap<>();
//        for (String bagName : bagsHolder.keySet()) {
//            ArrayList<String> connotes = bagsHolder.get(bagName);
//            connoteCounts.put(bagName, connotes.size());
//        }
//        return connoteCounts;
//    }

    public int getTotalConnoteCount() {
        int totalConnoteCount = 0;
        for (ArrayList<String> connotes : bagsHolder.values()) {
            totalConnoteCount += connotes.size();
        }
        return totalConnoteCount;
    }

    public ArrayList<ArrayHashModel<ArrayList<String>>> getArrayBagsData() {
        return this.utils.hashMapToArray(bagsHolder);
    }

    // secondary functions
    public boolean isConnoteInBAG() {
        return false;
    }

}

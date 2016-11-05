//
//  VoidDurationListVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class VoidDurationListVC: UITableViewController {
    //Links
    weak var DBC:PVRDataController!
    weak var SCH:PVRScheduler!
    weak var CFG:PVRConfig!

    //Data
    var arr_voidd:[PVRVoidDuration]!
    var utcell_id_voidd:String!
    var arr_voidd_idx:Int!

    //UI Elements
    @IBOutlet weak var barbtn_edit: UIBarButtonItem!
    @IBOutlet weak var barbtn_add: UIBarButtonItem!
    
    override func viewDidLoad() {
        //Init Links 
        let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appDelegate.DBC
        self.SCH = appDelegate.SCH
        self.CFG = appDelegate.CFG

        //Init Data
        self.utcell_id_voidd = "utcell.list.voidd"
        self.arr_voidd = Array<PVRVoidDuration>()
        self.arr_voidd_idx = 0

        super.viewDidLoad()
    }

    override func viewWillDisappear(_ animated: Bool) {
        //Update Data
        self.arr_voidd = self.DBC.sortedVoidDuration(sattr: PVRVoidDurationSort.begin)
        self.arr_voidd_idx = 0
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    //UITableView Delegate/Data Source Functions
    override func numberOfSections(in tableView: UITableView) -> Int {
        let sections = 1 //Only one section 
        return sections
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section
        {
        case 0: //First Section
            return self.DBC.DB.voidDuration.values.count
        default:
            abort() //Unhandled Section
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //Create/Update Table View Cell
        let cell = ((tableView.dequeueReusableCell(withIdentifier: self.utcell_id_voidd, for: indexPath)) as! VoidDurationListTBC)
        let voidd = self.arr_voidd[self.arr_voidd_idx]
        cell.updateData(name: voidd.name, begin: voidd.begin, duration: voidd.duration)

        //Update Data
        self.arr_voidd_idx! += 1
        if self.arr_voidd_idx >= self.arr_voidd.count
        {
            self.arr_voidd_idx = 0
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        //Can Edit any Row
        return true
    }

    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == UITableViewCellEditingStyle.delete {
            switch indexPath.section {
            case 0:
                let voidd = self.DBC.sortedVoidDuration(sattr: PVRVoidDurationSort.begin)[indexPath.row]
                let rst = self.DBC.deleteVoidDuration(name: voidd.name)
                assert(rst) //Terminates Executable if delete fails
            default:
                abort() //Unhandled Section
            }

            tableView.deleteRows(at: [indexPath], with: UITableViewRowAnimation.automatic)
        }
        else if editingStyle == UITableViewCellEditingStyle.insert {
            //Data Should be in databasel
            tableView.insertRows(at: [indexPath], with: UITableViewRowAnimation.automatic)
        }    
    }

    //UI Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let sge_idf = segue.identifier
        {
            switch sge_idf {
            case "uisge.voidblock.add":
                let voiddeditvc = (segue.destination as! VoidDurationEditVC)
                voiddeditvc.loadAddVoidDuration()
            case "uisge.voidblock.edit":
                let voiddeditvc = (segue.destination as! VoidDurationEditVC)
                let voidd = self.arr_voidd[(self.tableView.indexPathForSelectedRow?.row)!]
                voiddeditvc.loadEditVoidDuration(voidd: voidd)
            default:
                abort()
            }
        }
        else
        {
            abort()
        }
    }

}

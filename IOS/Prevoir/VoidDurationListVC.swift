//
//  VoidDurationListVC.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class VoidDurationListVC: UITableViewController {
    //Links
    weak var DBC:SKMDataController!
    weak var SCH:SKMScheduler!
    weak var CFG:SKMConfig!

    //Data
    var arr_voidd:[SKMVoidDuration]!

    var utcell_id_voidd:String!
    var utcell_id_voidd_null:String!

    //UI Elements
    @IBOutlet weak var barbtn_edit: UIBarButtonItem!
    @IBOutlet weak var barbtn_add: UIBarButtonItem!

    //Data Functions
    /*
     * public func updateData()
     * - Updates data to reflect database
    */
    public func updateData()
    {
        //Update Data
        self.DBC.updateVoidDuration()
        self.arr_voidd = self.DBC.sortedVoidDuration(sattr: SKMVoidDurationSort.begin)
    }

    /*
     * public func updateUI()
     * - Triggers a update to current data and UI update with that data
    */
    public func updateUI()
    {
        //Update UI
        self.tableView.reloadData()
    }
   
    //Event Functions
    override func viewDidLoad() {
        //Init Links 
        let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appDelegate.DBC
        self.SCH = appDelegate.SCH
        self.CFG = appDelegate.CFG

        //Init Data
        self.utcell_id_voidd = "uitcell.list.voidd"
        self.utcell_id_voidd_null = "uitcell.list.voidd.null"

        self.arr_voidd = self.DBC.sortedVoidDuration(sattr: SKMVoidDurationSort.begin)

        super.viewDidLoad()
    }

    override func viewWillAppear(_ animated: Bool) {
        //Update Data & UI
        self.updateData()
        self.updateUI()
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
        //Prepare Data
        self.updateData()

        switch section
        {
        case 0: //First Section
            return  self.arr_voidd.count
        default:
            abort() //Unhandled Section
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if self.arr_voidd.count > 0
        {
            //Create/Update Table View Cell
            let cell = ((tableView.dequeueReusableCell(withIdentifier: self.utcell_id_voidd, for: indexPath)) as! VoidDurationListTBC)
            let voidd = self.arr_voidd[indexPath.row]
            //NOTE: Duration adjustment due to void duration preadjustment
            cell.updateUI(name: voidd.name, begin: voidd.begin, duration: voidd.duration + 1)

            return cell
        }
        else
        {
            //Empty Void Duration Cell
            let cell = tableView.dequeueReusableCell(withIdentifier: self.utcell_id_voidd_null, for: indexPath)
            return cell
        }
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        if self.arr_voidd.count <= 0
        {
            return false //Cannot Edit Null Cell
        }
        else
        {
            return true
        }
    }

    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == UITableViewCellEditingStyle.delete {
            switch indexPath.section {
            case 0:
                let voidd = self.DBC.sortedVoidDuration(sattr: SKMVoidDurationSort.begin)[indexPath.row]
                let rst = self.DBC.deleteVoidDuration(name: voidd.name)
                assert(rst) //Terminates Executable if delete fails
            default:
                abort() //Unhandled Section

            }

            //Update Data
            self.updateData()
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
                let _ = voiddeditvc.view //Force View Load
                voiddeditvc.loadAddVoidDuration()
            case "uisge.voidblock.edit":
                let voiddeditvc = (segue.destination as! VoidDurationEditVC)
                let _ = voiddeditvc.view //Force View Load
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


    @IBAction func unwind_voidDurationList(sge:UIStoryboardSegue)
    {
        self.updateUI()
    }
}

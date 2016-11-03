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
        
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    //UITableDataSource Protocol Functions
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
            abort() //Only one section
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //Update Data
        if self.arr_voidd.count <= 0
        {
            self.arr_voidd = self.DBC.sortedVoidDuration(sattr: PVRVoidDurationSort.begin)
        }

        //Create/Update Table View Cell
        let cell = ((tableView.dequeueReusableCell(withIdentifier: self.utcell_id_voidd, for: indexPath)) as! VoidDurationListTBC)
        let voidd = self.arr_voidd.removeFirst()
        cell.updateData(name: voidd.name, begin: voidd.begin, duration: voidd.duration)

        return cell
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }

    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

//
//  SettingListVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class SettingListVC: UITableViewController {
    //Link
    weak var DBC:PVRDataController! /* Link to Data Controller */
    weak var CFG:PVRConfig! /* Link to Config Object */

    //UI Elements
    @IBOutlet weak var switch_shake: UISwitch!

    //Functions
    //Events
    override func viewDidLoad() {
        super.viewDidLoad()
        //Init Links
        let appdelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appdelegate.DBC
        self.CFG = appdelegate.CFG
    }

    override func viewWillAppear(_ animated: Bool) {
        self.switch_shake.setOn(true, animated: false)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    //UI Events
    @IBAction func switch_toggle_shake(_ sender: UISwitch) {
        let cfg_val = NSNumber(booleanLiteral: sender.isOn)
        CFG.commitSetting(set: PVRSetting.ui_shake, val: cfg_val)
    }
}

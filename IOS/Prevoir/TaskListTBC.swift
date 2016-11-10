//
//  TaskListTBC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 9/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class TaskListTBC: UITableViewCell {

    //UI Element
    @IBOutlet weak var label_subject: UILabel!
    @IBOutlet weak var label_name: UILabel!
    @IBOutlet weak var label_status: UILabel!

    //Functions
    //Data Functions
    /*
     * public func updateData(name:String,subject:String,completion:Double)
     * - Update UI elements to specified data
     * [Arguments]
     * name - name of the task
     * subject - subject of the task
     * completion - completion of the task
    */
    public func updateData(name:String,subject:String,completion:Double)
    {
        //Update UI to reflect data
        self.label_name.text = name
        self.label_subject.text = subject

        self.label_status.text = "\(completion * 100)%"
        if completion < 0.50
        {
            self.label_status.textColor = UIColor.red
        }
        else if completion < 1.00
        {
            self.label_status.textColor = UIColor.yellow
        }
        else
        {
            self.label_status.textColor = UIColor.green
        }
    }

    //Event Functions
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}

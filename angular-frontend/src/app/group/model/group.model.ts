export class Group {
    _id: number;
    name: String;
    description: String;
    creationDate: Date;
    isSuspended: boolean;
    suspendedReason: String;

    constructor(obj?: any) {
        this._id = obj && obj._id || null;
        this.name = obj && obj.name || null;
        this.description = obj && obj.description || null;
        this.creationDate = obj && obj.creationDate || null;
        this.isSuspended = obj && obj.isSuspended || null;
        this.suspendedReason = obj && obj.suspendedReason || null;
    }
}

import { GroupIndex } from "./groupIndex.model";

export class Hit {
    highlights: Map<string, string[]>;
    source: GroupIndex;

    constructor(obj: {
        highlights?: Map<string, string[]>,
        source?: GroupIndex,
    } = {}) {
        this.highlights = obj.highlights || null as unknown as Map<string, string[]>;
        this.source = obj.source || null as unknown as GroupIndex;
    }
}

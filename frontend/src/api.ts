
import axios from "axios";


const api = axios.create({
  baseURL: "/api", 
  headers: { "Content-Type": "application/json" },
});


export type Location = {
  id: number;
  name: string;
  country: string;
  city: string;
  locationCode: string;
};

export type LocationPage = {
  locations?: Location[];     
  content?: Location[];       
  pageNum: number;
  pageSize: number;
  hasNext?: boolean;          
  totalElements?: number;     
};

export const LocationApi = {
  async getAll(
    pageNum: number,
    pageSize: number,
    sortBy?: string,
    direction?: "asc" | "desc"
  ): Promise<LocationPage> {
    const params: Record<string, any> = {
      pageNum: Number.isFinite(pageNum) ? pageNum : 0,
      pageSize: Number.isFinite(pageSize) ? pageSize : 10,
    };
    if (sortBy) params.sortBy = sortBy;
    if (direction) params.direction = direction;

    const res = await api.get("/locations", { params });
    return res.data as LocationPage;
  },

  async create(loc: Omit<Location, "id">): Promise<Location> {
    const res = await api.post("/locations", loc);
    return res.data as Location;
  },


  async update(id: number, loc: Omit<Location, "id">): Promise<Location> {
    const res = await api.put(`/locations/${id}`, loc);
    return res.data as Location;
  },


  async remove(id: number): Promise<void> {
    await api.delete(`/locations/${id}`);
  },
};


export const LocationDropdown = {
  async listAllNames(): Promise<
    { id: number; name: string; city: string; country: string; locationCode: string }[]
  > {
    const page = await LocationApi.getAll(0, 1000, "name", "asc");
    const items = (page as any).locations ?? (page as any).content ?? [];
    return items.map((l: any) => ({
      id: l.id,
      name: l.name,
      city: l.city,
      country: l.country,
      locationCode: l.locationCode,
    }));
  },
};



export const TransportationApi = {
  
  async getTypes(): Promise<string[]> {
    try {
      const res = await api.get("/transportations/types");
      return res.data as string[]; 
    } catch {
    
      return ["FLIGHT", "BUS", "SUBWAY", "UBER"];
    }
  },

 
  async getAll(
    pageNum: number,
    pageSize: number,
    sortBy?: string,
    direction?: "asc" | "desc"
  ): Promise<any> {
    const params: Record<string, any> = {
      pageNum: Number.isFinite(pageNum) ? pageNum : 0,
      pageSize: Number.isFinite(pageSize) ? pageSize : 10,
    };
    if (sortBy) params.sortBy = sortBy;
    if (direction) params.direction = direction;

    const res = await api.get("/transportations", { params });
    return res.data;
  },


  async create(payload: {
    originLocationId: number;
    destinationLocationId: number;
    transportationType: string;
    operatingDays: number[];
  }): Promise<any> {
    const res = await api.post("/transportations", payload);
    return res.data;
  },

  async update(
    id: number,
    payload: {
      originLocationId: number;
      destinationLocationId: number;
      transportationType: string;
      operatingDays: number[];
    }
  ): Promise<any> {
    const res = await api.put(`/transportations/${id}`, payload);
    return res.data;
  },


  async delete(id: number): Promise<void> {
    await api.delete(`/transportations/${id}`);
  },
};



export type RouteFindItem = {
  originLocation: Location;
  destinationLocation: Location;
  transportations: Array<{
    id: number;
    originLocation: Location;
    destinationLocation: Location;
    transportationType: string;
    operatingDays: number[];
  }>;
};

export const RouteApi = {

  async find(params: {
    originLocationId: number;
    destinationLocationId: number;
    date?: string; 
  }): Promise<RouteFindItem[]> {
    const { originLocationId, destinationLocationId, date } = params;
    const query: Record<string, any> = { originLocationId, destinationLocationId };
    if (date) query.date = date; 

    const res = await api.get("/routes/find", { params: query });
    return res.data as RouteFindItem[];
  },
};

export default api;